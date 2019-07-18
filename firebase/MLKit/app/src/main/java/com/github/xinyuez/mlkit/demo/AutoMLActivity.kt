package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import io.fotoapparat.Fotoapparat
import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor
import io.fotoapparat.selector.back
import io.fotoapparat.selector.highestSensorSensitivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_ocr.*
import kotlinx.android.synthetic.main.content_automl.*
import kotlinx.android.synthetic.main.content_face.camera_view
import java.io.IOException

class AutoMLActivity : AppCompatActivity(), FrameProcessor {

    private var _detector: FirebaseVisionImageLabeler? = null

    private val _conditions: FirebaseModelDownloadConditions by lazy {
        FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build()
    }

    private val _remoteModel: FirebaseRemoteModel by lazy {
        FirebaseRemoteModel.Builder(MODEL)
            .enableModelUpdates(true)
            .setInitialDownloadConditions(_conditions)
            .setUpdatesDownloadConditions(_conditions)
            .build()
    }

    private val _localModel: FirebaseLocalModel by lazy {
        FirebaseLocalModel.Builder(MODEL)
            .setAssetFilePath("automl/manifest.json")
            .build()
    }

    private val _labelerOptions: FirebaseVisionOnDeviceAutoMLImageLabelerOptions by lazy {
        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
            .setLocalModelName(MODEL)
            .setRemoteModelName(MODEL)
            .setConfidenceThreshold(CONFIDENCE_THRESHOLD)
            .build()
    }

    init {
        FirebaseModelManager.getInstance().registerRemoteModel(_remoteModel)
        FirebaseModelManager.getInstance().registerLocalModel(_localModel)

        FirebaseModelManager.getInstance().downloadRemoteModelIfNeeded(_remoteModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Download remote AutoML model success.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val downloadingError = "Error downloading remote model."
                    Log.e(TAG, downloadingError, task.exception)
                    Toast.makeText(applicationContext, downloadingError, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val _fotoapparat by lazy {
        Fotoapparat.with(applicationContext)
            .into(camera_view)
            .lensPosition(back())
            .sensorSensitivity(highestSensorSensitivity())
            .frameProcessor(this)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_automl)
        setSupportActionBar(ocr_appbar)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    override fun onStart() {
        super.onStart()
        _fotoapparat.start()
    }

    override fun onStop() {
        _detector.closeSafely()
        super.onStop()
        _fotoapparat.stop()
    }

    override fun process(frame: Frame) {
        process(
            frame.image,
            frame.size.width,
            frame.size.height
        )
    }

    private fun process(
        target: ByteArray,
        width: Int,
        height: Int,
        rotation: Int = FirebaseVisionImageMetadata.ROTATION_270
    ) {
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(width) // 480x360 is typically sufficient for
            .setHeight(height) // image recognition
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(rotation)
            .build()

        val firebaseVisionBitmap: FirebaseVisionImage =
            FirebaseVisionImage.fromByteArray(target, metadata)
        _detector = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(_labelerOptions)
        _detector.process(firebaseVisionBitmap)
    }

    private fun FirebaseVisionImageLabeler?.closeSafely() {
        try {
            this?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close the image labeler: $e")
        }
    }

    private fun FirebaseVisionImageLabeler?.process(firebaseVisionBitmap: FirebaseVisionImage) {
        this?.let {
            processImage(firebaseVisionBitmap)
                .addOnSuccessListener(this@AutoMLActivity::process)
                .addOnFailureListener { e ->
                    Log.d(TAG, e.message)
                }
        }
    }

    private fun process(labels: List<FirebaseVisionImageLabel>) {
        return when {
            labels.size == 1 -> {
                val group: Pair<String, Float> = labels[0].text to labels[0].confidence
                group.showOutput()
            }
            labels.size > 1 -> {
                val group1: Pair<String, Float> = labels[0].text to labels[0].confidence
                val group2: Pair<String, Float> = labels[1].text to labels[1].confidence

                val win = if (group1.second > group2.second) group1 else group2
                win.showOutput()
            }
            else -> dismissOutput()
        }
    }

    private fun Pair<String, Float>.showOutput() {
        when {
            "t6" in this.first -> {
                automl_t6.text = this.second.toString()
                automl_t6.visibility = View.VISIBLE
                automl_pluto.visibility = View.GONE
            }
            "pluto" in this.first -> {
                automl_pluto.text = this.second.toString()
                automl_pluto.visibility = View.VISIBLE
                automl_t6.visibility = View.GONE
            }
        }
    }

    private fun dismissOutput() {
        automl_pluto.visibility = View.GONE
        automl_t6.visibility = View.GONE
    }

    companion object {
        private const val MODEL = "MOIA"
        private const val CONFIDENCE_THRESHOLD = 0.5f
        private const val TAG = "mlkit-automl"

        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, AutoMLActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}