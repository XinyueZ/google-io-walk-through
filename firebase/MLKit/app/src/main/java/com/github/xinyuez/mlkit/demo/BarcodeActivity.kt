package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
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
import kotlinx.android.synthetic.main.content_face.camera_view

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

    override fun process(frame: Frame) {

    }

    companion object {
        internal const val TAG = "mlkit-barcode"
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, BarcodeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}