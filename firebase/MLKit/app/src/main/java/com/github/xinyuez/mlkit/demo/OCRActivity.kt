package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_ocr.*
import kotlinx.android.synthetic.main.content_ocr.*

class OCRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)
        setSupportActionBar(ocr_appbar)

        add_photo_fab.setOnClickListener { _ ->
            openOnDevicePhotoApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    private fun openOnDevicePhotoApp() {
        with(Intent(Intent.ACTION_PICK)) {
            type = "image/*"
            startActivityForResult(Intent.createChooser(this, getString(R.string.select_photo)), PICK_IMAGE)
        }
    }

    private fun process(bitmap: Bitmap) {
        ocr_photo_iv.setImageBitmap(bitmap)
    }

    private fun handlePhotoSelection(intent: Intent, block: (bitmap: Bitmap) -> Unit) {
        val pickedImage = intent.data
        block(MediaStore.Images.Media.getBitmap(this.contentResolver, pickedImage))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            PICK_IMAGE -> {
                intent?.let { it ->
                    handlePhotoSelection(it) { bitmap ->
                        process(bitmap)
                    }
                } ?: kotlin.run {
                    super.onActivityResult(requestCode, resultCode, intent)
                    Snackbar.make(add_photo_fab, R.string.select_photo_fail, Snackbar.LENGTH_LONG).show()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, intent)
        }
    }

    companion object {
        internal const val PICK_IMAGE = 0x2
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, OCRActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}