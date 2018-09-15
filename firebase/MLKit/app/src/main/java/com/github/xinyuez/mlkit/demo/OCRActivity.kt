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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.activity_ocr.ocr_appbar
import kotlinx.android.synthetic.main.activity_ocr.open_file_fab
import kotlinx.android.synthetic.main.activity_ocr.open_file_iv
import kotlinx.android.synthetic.main.content_ocr.ocr_photo_iv
import java.util.Arrays

class OCRActivity : AppCompatActivity() {
    /**
     * TODO set [false] for cloud OCR.
     */
    private var onDevice = true

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

    private fun getTextRecognizer(): FirebaseVisionTextRecognizer {
        return when (onDevice) {
            true -> {
                Toast.makeText(applicationContext, "on device OCR", Toast.LENGTH_LONG).show()
                FirebaseVision.getInstance().onDeviceTextRecognizer
            }
            else -> {
                Toast.makeText(applicationContext, "cloud OCR", Toast.LENGTH_LONG).show()
                val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
                    .setLanguageHints(Arrays.asList("en", "de"))
                    .build()
                FirebaseVision.getInstance()
                    .getCloudTextRecognizer(options)
            }
        }
    }

    private fun process(target: Bitmap) {
        ocr_photo_iv.setImageBitmap(target)
        val firebaseVisionBitmap = FirebaseVisionImage.fromBitmap(target)
        val textRecognizer = getTextRecognizer()
        target.process(firebaseVisionBitmap, textRecognizer)
    }

    private fun process(target: Uri) {
        ocr_photo_iv.setImageURI(target)
        val firebaseVisionBitmap = FirebaseVisionImage.fromFilePath(application, target)
        val textRecognizer = getTextRecognizer()
        target.process(firebaseVisionBitmap, textRecognizer)
    }

    private fun handleBitmapSelection(intent: Intent, block: (bitmap: Bitmap) -> Unit) {
        val uri = intent.data
        block(MediaStore.Images.Media.getBitmap(this.contentResolver, uri))
    }

    private fun handleUriSelection(intent: Intent, block: (uri: Uri) -> Unit) {
        val uri = intent.data
        uri?.apply { block(this) }
    }

    @Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")
    private fun FirebaseVisionText.TextBlock.handle(
        canvas: Canvas,
        paint: Paint
    ): FirebaseVisionText.TextBlock {
        val blockText = text
        val blockConfidence = confidence
        val blockLanguages = recognizedLanguages
        val blockCornerPoints = cornerPoints
        val blockFrame = boundingBox

        paint.color = Color.BLUE
        blockFrame?.apply { canvas.drawRect(this, paint) }

        Log.d(TAG, blockText)
        return this
    }

    @Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")
    private fun FirebaseVisionText.Line.handle(
        canvas: Canvas,
        paint: Paint
    ): FirebaseVisionText.Line {
        val lineText = text
        val lineConfidence = confidence
        val lineLanguages = recognizedLanguages
        val lineCornerPoints = cornerPoints
        val lineFrame = boundingBox

        paint.color = Color.MAGENTA
        lineFrame?.apply { canvas.drawRect(this, paint) }
        return this
    }

    @Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")
    private fun FirebaseVisionText.Element.handle(
        canvas: Canvas,
        paint: Paint
    ): FirebaseVisionText.Element {
        val elementText = text
        val elementConfidence = confidence
        val elementLanguages = recognizedLanguages
        val elementCornerPoints = cornerPoints
        val elementFrame = boundingBox
        return this
    }

    private fun process(result: FirebaseVisionText) {
        ocr_photo_iv.drawable.toBitmap().copy(Bitmap.Config.ARGB_8888, true).let { bitmap ->
            Canvas(bitmap).let { canvas ->
                with(Paint()) {
                    style = Paint.Style.STROKE
                    isAntiAlias = true

                    result.textBlocks.forEach { block ->
                        block.handle(canvas, this)
                        block.lines.forEach { line ->
                            line.handle(canvas, this)
                            line.elements.forEach { element ->
                                element.handle(canvas, this)
                            }
                        }
                    }
                }
            }
            ocr_photo_iv.setImageBitmap(bitmap)
        }
    }

    private fun Uri.process(
        visionBitmap: FirebaseVisionImage,
        textRecognizer: FirebaseVisionTextRecognizer
    ) {
        textRecognizer.processImage(visionBitmap)
            .addOnSuccessListener(this@OCRActivity::process)
            .addOnFailureListener {
                onDevice = !onDevice
                process(this)
            }
    }

    private fun Bitmap.process(
        visionBitmap: FirebaseVisionImage,
        textRecognizer: FirebaseVisionTextRecognizer
    ) {
        textRecognizer.processImage(visionBitmap)
            .addOnSuccessListener(this@OCRActivity::process)
            .addOnFailureListener {
                onDevice = !onDevice
                process(this)
            }
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
                    Snackbar.make(ocr_photo_iv, R.string.select_photo_fail, Snackbar.LENGTH_LONG)
                        .show()
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