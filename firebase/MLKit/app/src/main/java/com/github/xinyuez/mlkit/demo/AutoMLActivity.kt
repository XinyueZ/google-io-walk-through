package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import io.fotoapparat.Fotoapparat
import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor
import io.fotoapparat.selector.back
import io.fotoapparat.selector.highestSensorSensitivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_ocr.*
import kotlinx.android.synthetic.main.content_face.*

class AutoMLActivity : AppCompatActivity(), FrameProcessor {

    private val _conditions: FirebaseModelDownloadConditions by lazy {
        FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build()
    }

    private val _remoteModel: FirebaseRemoteModel by lazy {
        FirebaseRemoteModel.Builder("my_remote_model").build()
    }

    private val _localModel: FirebaseLocalModel by lazy {
        FirebaseLocalModel.Builder("my_local_model")
            .setAssetFilePath("manifest.json")
            .build()
    }

    init {
        FirebaseModelManager.getInstance().registerRemoteModel(_remoteModel)
        FirebaseModelManager.getInstance().registerLocalModel(_localModel)
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
        setContentView(R.layout.activity_automl)
        setSupportActionBar(ocr_appbar)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }

    override fun process(frame: Frame) {
        process(
            frame.image,
            frame.size.width,
            frame.size.height,
            FirebaseVisionImageMetadata.ROTATION_270
        )
    }

    private fun process(target: ByteArray, width: Int, height: Int, rotation: Int) {
    }

    companion object {

        internal const val TAG = "mlkit-automl"

        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, AutoMLActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}