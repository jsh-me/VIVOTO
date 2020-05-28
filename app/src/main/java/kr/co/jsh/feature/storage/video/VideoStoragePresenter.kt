package kr.co.jsh.feature.storage.video

import android.annotation.SuppressLint
import io.reactivex.schedulers.Schedulers
import kr.co.data.entity.room.VideoStorage
import kr.co.domain.api.usecase.AllDeleteVideoDataBaseUseCase
import kr.co.domain.api.usecase.AllLoadVideoDataBaseUseCase
import kr.co.domain.api.usecase.GetAllVideoResultListUseCase
import kr.co.domain.api.usecase.InsertVideoDataBaseUseCase
import timber.log.Timber


class VideoStoragePresenter(override var view: VideoStorageContract.View,
                            private var getAllVideoResultIUseCase: GetAllVideoResultListUseCase,
                            private var insertVideoDataBaseUseCase: InsertVideoDataBaseUseCase,
                            private var allLoadVideoDataBaseUseCase: AllLoadVideoDataBaseUseCase,
                            private var allDeleteVideoDataBaseUseCase: AllDeleteVideoDataBaseUseCase)
    :VideoStorageContract.Presenter{

    private val addRoomDBVideoStorage : ArrayList<List<String>> = ArrayList()
    private val addServerVideoStorage : ArrayList<List<String>> = ArrayList()
    private val resultVideoList = ArrayList<String>()

    init{
        loadVideoStorage()
    }

    //when response 200
    @SuppressLint("CheckResult")
    override fun getServerVideoResult() {
        resultVideoList.clear()
        allDeleteVideoStorage()

        getAllVideoResultIUseCase.getAllVideoResult()
            .subscribe({
                it.datas.list.map{
                    it.resultFile?.objectPid?.let{
                        resultVideoList.add("http://192.168.0.188:8080/file/fileDownload.do?objectPid=${it}")
                        resultVideoList.add(it)
                        addServerVideoStorage.add(resultVideoList)
                        insertDataBase(addServerVideoStorage)
                     }
                    }
                view.setVideoResult(addServerVideoStorage)
            },{
                Timber.e(it.localizedMessage)
            })
    }

    //when response 500
    override fun getLocalVideoResult() {
        view.setVideoResult(addRoomDBVideoStorage)
    }

    //load db
    @SuppressLint("CheckResult")
    private fun loadVideoStorage(){
        resultVideoList.clear()

        allLoadVideoDataBaseUseCase.allLoad()
            .subscribeOn(Schedulers.io())
            .subscribe({
                it.map{
                    resultVideoList.add(it.path)
                    resultVideoList.add(it.filename)
                    addRoomDBVideoStorage.add(resultVideoList) //set 함수
                }
                    view.setVideoResult(addRoomDBVideoStorage)
            },{
                Timber.e( "Error getting info from interactor (video)")
            })
    }

    //all delete db
    private fun allDeleteVideoStorage(){
        allDeleteVideoDataBaseUseCase.allDelete()
    }

    //insert db
    private fun insertDataBase(storage: ArrayList<List<String>>){
        insertVideoDataBaseUseCase.insert(VideoStorage(null, storage[storage.size-1][0], storage[storage.size-1][1]))
    }
}