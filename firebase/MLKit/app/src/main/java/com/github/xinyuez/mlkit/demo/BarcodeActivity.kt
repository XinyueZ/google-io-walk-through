package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri.parse
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.xinyuez.mlkit.demo.GraphicOverlay.Graphic
import com.github.xinyuez.mlkit.demo.GraphicOverlay.Graphic.OnGraphicClickListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor
import io.fotoapparat.selector.back
import io.fotoapparat.selector.firstAvailable
import io.fotoapparat.selector.highestSensorSensitivity
import io.fotoapparat.selector.off
import io.fotoapparat.selector.torch
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.content_barcode.overlay
import kotlinx.android.synthetic.main.content_face.camera_view
import java.lang.ref.WeakReference

class BarcodeActivity : AppCompatActivity(), FrameProcessor {
    private var flashOn = false
    private var barcodeDetector: FirebaseVisionBarcodeDetector? = null

    private val options by lazy {
        FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
            .build()
    }

    private val fotoapparat by lazy {
        Fotoapparat.with(applicationContext)
            .into(camera_view)
            .lensPosition(back())
            .sensorSensitivity(highestSensorSensitivity())
            .frameProcessor(this)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        barcodeDetector?.close()
        super.onStop()
        fotoapparat.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    @Suppress("UNUSED_PARAMETER")
    fun flash(v: View) {
        fotoapparat.updateConfiguration(
            UpdateConfiguration(
                flashMode = if (flashOn) {
                    flashOn = false
                    firstAvailable(
                        off()
                    )
                } else {
                    flashOn = true
                    torch()
                }
            )
        )
    }

    private fun process(target: ByteArray, width: Int, height: Int, rotation: Int) {
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(width)
            .setHeight(height)
            .setRotation(rotation)
            .build()

        val firebaseVisionBitmap = FirebaseVisionImage.fromByteArray(target, metadata)
        barcodeDetector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        barcodeDetector.process(firebaseVisionBitmap)
    }

    private fun FirebaseVisionBarcodeDetector?.process(firebaseVisionBitmap: FirebaseVisionImage) {
        this?.let {
            detectInImage(firebaseVisionBitmap)
                .addOnSuccessListener(this@BarcodeActivity::process)
                .addOnFailureListener { e ->
                    Log.d(BarcodeActivity.TAG, e.message)
                }
        }
    }

    private fun process(barcodes: List<FirebaseVisionBarcode>) {
        overlay.clear()

        barcodes.forEach { barcode ->
            val graphic = BarcodeGraphic(overlay, barcode)
            overlay.add(graphic)
            graphic.setListener(graphic, LinkOpener(this, barcode))
        }
    }

    override fun process(frame: Frame) {
        process(
            frame.image,
            frame.size.width,
            frame.size.height,
            FirebaseVisionImageMetadata.ROTATION_270
        )
    }

    private class LinkOpener(activity: Activity, private val barcode: FirebaseVisionBarcode) :
        OnGraphicClickListener {
        private val activityWrapper = WeakReference(activity)

        @MainThread
        override fun onClick(graphic: Graphic) {
            graphic.removeListener()

            activityWrapper.get()?.let { activity ->
                when (barcode.valueType) {
                    FirebaseVisionBarcode.TYPE_URL -> {
                        barcode.url?.url?.let { openUrl ->
                            Intent(Intent.ACTION_VIEW, parse(openUrl)).apply {
                                flags = intentFlags
                                ActivityCompat.startActivity(activity, this, Bundle.EMPTY)
                            }
                        }
                    }
                    else -> {
                        activity.runOnUiThread {
                            Intent(
                                Intent.ACTION_VIEW, parse(
                                    "https://www.ean-search.org/?q=${barcode.displayValue}/"
                                )
                            ).apply {
                                flags = intentFlags
                                ActivityCompat.startActivity(activity, this, Bundle.EMPTY)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        internal const val TAG = "mlkit-barcode"
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, BarcodeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }

        internal const val intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}

// https://github.com/firebase/quickstart-android/blob/master/mlkit/app/src/main/java/com/google/firebase/samples/apps/mlkit/kotlin/barcodescanning/BarcodeGraphic.kt
private class BarcodeGraphic(overlay: GraphicOverlay, barcode: FirebaseVisionBarcode) :
    GraphicOverlay.Graphic(overlay) {

    companion object {
        private const val TEXT_COLOR = Color.RED
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
    }

    private var rectPaint: Paint
    private var barcodePaint: Paint
    private val barcode: FirebaseVisionBarcode?

    private var rect: RectF? = null

    init {
        this.barcode = barcode

        rectPaint = Paint()
        rectPaint.color = TEXT_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH

        barcodePaint = Paint()
        barcodePaint.color = TEXT_COLOR
        barcodePaint.textSize = TEXT_SIZE
        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    /**
     * Draws the barcode block annotations for position, size, and raw value on the supplied canvas.
     */
    override fun draw(canvas: Canvas) {
        if (barcode == null) {
            throw IllegalStateException("Attempting to draw a null barcode.")
        }

        // Draws the bounding box around the BarcodeBlock.
        rect = RectF(barcode.boundingBox).apply {
            left = translateX(left)
            top = translateY(top)
            right = translateX(right)
            bottom = translateY(bottom)
            canvas.drawRect(this, rectPaint)

            // Renders the barcode at the bottom of the box.
            barcode.rawValue?.let { value ->
                canvas.drawText(value, left, bottom, barcodePaint)
            }
        }
    }

    override fun contains(x: Float, y: Float) = rect?.contains(x, y) ?: false
}