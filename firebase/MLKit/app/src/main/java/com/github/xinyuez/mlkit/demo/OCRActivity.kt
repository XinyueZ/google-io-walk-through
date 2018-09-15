package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.activity_ocr.ocr_appbar
import kotlinx.android.synthetic.main.activity_ocr.open_file_fab
import kotlinx.android.synthetic.main.activity_ocr.open_file_iv
import kotlinx.android.synthetic.main.content_ocr.ocr_photo_iv

class OCRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)
        setSupportActionBar(ocr_appbar)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openOnDevicePhotoApp(v: View) {
        with(Intent(Intent.ACTION_PICK)) {
            type = "image/*"
            startActivityForResult(
                Intent.createChooser(this, getString(R.string.select_photo)),
                PICK_IMAGE
            )
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
        uri?.apply { block(this) }
    }

    private fun process(result: FirebaseVisionText) {
        val mutableBitmap = ocr_photo_iv.drawable.toBitmap().copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.isAntiAlias = true

        for (block in result.textBlocks) {
            val blockText = block.text

            Log.d(TAG, "###################################")
            Log.d(TAG, "blockText: $blockText")
            Log.d(TAG, "###################################")

            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox

            canvas.drawRect(blockFrame,paint)

            for (line in block.lines) {
                val lineText = line.text

                Log.d(TAG, "###################################")
                Log.d(TAG, "lineText: $lineText")
                Log.d(TAG, "###################################")

                val lineConfidence = line.confidence
                val lineLanguages = line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox

                canvas.drawRect(lineFrame,paint)

                for (element in line.elements) {
                    val elementText = element.text
                    Log.d(TAG, "elementText: $elementText")

                    val elementConfidence = element.confidence
                    val elementLanguages = element.recognizedLanguages
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox

                    canvas.drawRect(elementFrame,paint)
                }
            }
        }
        ocr_photo_iv.setImageBitmap(mutableBitmap)
    }

    private fun process(visionBitmap: FirebaseVisionImage, textRecognizer: FirebaseVisionTextRecognizer) {
        textRecognizer.processImage(visionBitmap)
            .addOnSuccessListener(::process)
            .addOnFailureListener { Snackbar.make(ocr_photo_iv, R.string.process_fail, Snackbar.LENGTH_LONG).show() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            PICK_IMAGE -> {
                intent?.let { it ->
                    handleUriSelection(it, ::process)
                    open_file_iv.visibility = View.GONE
                    open_file_fab.visibility = View.VISIBLE
                } ?: kotlin.run {
                    super.onActivityResult(requestCode, resultCode, intent)
                    Snackbar.make(ocr_photo_iv, R.string.select_photo_fail, Snackbar.LENGTH_LONG).show()
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