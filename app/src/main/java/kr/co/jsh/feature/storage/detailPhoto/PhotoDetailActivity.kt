package kr.co.jsh.feature.storage.detailPhoto

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kr.co.domain.globalconst.Consts
import kr.co.jsh.R
import kr.co.jsh.databinding.ActivityDetailPhotoResultBinding
import kr.co.jsh.utils.permission_verQ.ScopeStorageFileUtil

class PhotoDetailActivity :AppCompatActivity(){
    private lateinit var binding : ActivityDetailPhotoResultBinding
    private var resourceBitmap: Bitmap ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDataBinding()
        initView()
    }

    private fun setupDataBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_photo_result)
        binding.resultPhoto = this@PhotoDetailActivity
    }

    private fun initView(){
        val result = intent.getStringExtra(Consts.DETAIL_PHOTO)
        Glide.with(this).asBitmap().load(result).listener(object: RequestListener<Bitmap>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resourceBitmap = resource
                return false
            }
        }).into(binding.resultImageDetail)
    }

    fun savePhoto(){
        val displayName = "${System.currentTimeMillis()}.jpg"
        val mimeType = "image/jpeg"
        val compressFormat = Bitmap.CompressFormat.JPEG
        resourceBitmap?.let{ ScopeStorageFileUtil.addPhotoAlbum(resourceBitmap!!, displayName, mimeType, compressFormat, this) }
        Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show()
    }

    fun backButton(){
        finish()
    }

}