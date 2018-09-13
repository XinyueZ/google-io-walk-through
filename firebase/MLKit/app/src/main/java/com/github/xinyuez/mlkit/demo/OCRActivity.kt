package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_ocr.*

class OCRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)
        setSupportActionBar(ocr_appbar)

        add_photo_fab.setOnClickListener { _ ->
            openOnDevicePhotoApp()
        }
    }

    private fun openOnDevicePhotoApp() {
        with(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
        ) {
            startActivityForResult(Intent.createChooser(this, getString(R.string.select_photo)), PICK_IMAGE)
        }
    }

    private fun process(bitmap: Bitmap?) {
        bitmap?.let {
            Snackbar.make(add_photo_fab, "select bitmap", Snackbar.LENGTH_LONG).show()
        } ?: kotlin.run {
            Snackbar.make(add_photo_fab, R.string.select_photo_fail, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_IMAGE -> process(data?.extras?.getParcelable("data") as Bitmap)
            else -> super.onActivityResult(requestCode, resultCode, data)
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