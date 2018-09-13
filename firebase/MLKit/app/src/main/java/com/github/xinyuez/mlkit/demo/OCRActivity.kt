package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
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

    private fun process(target: Bitmap) {
        ocr_photo_iv.setImageBitmap(target)
        val firebaseVisionBitmap = FirebaseVisionImage.fromBitmap(target)
        val textRecognizer = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        process(firebaseVisionBitmap, textRecognizer)
    }

    private fun process(target: Uri) {
        ocr_photo_iv.setImageURI(target)
        val firebaseVisionBitmap = FirebaseVisionImage.fromFilePath(application, target)
        val textRecognizer = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        process(firebaseVisionBitmap, textRecognizer)
    }

    private fun handlePhotoSelection(intent: Intent, block: (bitmap: Bitmap) -> Unit) {
        val uri = intent.data
        block(MediaStore.Images.Media.getBitmap(this.contentResolver, uri))
    }

    private fun handleUriSelection(intent: Intent, block: (uri: Uri) -> Unit) {
        val uri = intent.data
        block(uri!!)
    }

    private fun process(result: FirebaseVisionText) {
        for (block in result.textBlocks) {
            val blockText = block.text
            Log.d(TAG, "line: $blockText")

            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                Log.d(TAG, "line: $lineText")

                val lineConfidence = line.confidence
                val lineLanguages = line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    Log.d(TAG, "line: $elementText")

                    val elementConfidence = element.confidence
                    val elementLanguages = element.recognizedLanguages
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
    }

    private fun process(visionBitmap: FirebaseVisionImage, textRecognizer: FirebaseVisionTextRecognizer) {
        textRecognizer.processImage(visionBitmap)
            .addOnSuccessListener(::process)
            .addOnFailureListener { Snackbar.make(add_photo_fab, R.string.process_fail, Snackbar.LENGTH_LONG).show() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            PICK_IMAGE -> {
                intent?.let { it ->
                    handleUriSelection(it, ::process)
                } ?: kotlin.run {
                    super.onActivityResult(requestCode, resultCode, intent)
                    Snackbar.make(add_photo_fab, R.string.select_photo_fail, Snackbar.LENGTH_LONG).show()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, intent)
        }
    }

    companion object {
        internal const val TAG = "ocr"
        internal const val PICK_IMAGE = 0x2
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, OCRActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}