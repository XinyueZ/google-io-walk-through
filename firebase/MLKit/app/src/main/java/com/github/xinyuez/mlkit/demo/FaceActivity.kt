package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor
import io.fotoapparat.selector.firstAvailable
import io.fotoapparat.selector.off
import io.fotoapparat.selector.torch
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.activity_face.face_appbar
import kotlinx.android.synthetic.main.content_face.camera_view
import kotlinx.android.synthetic.main.content_face.snapshot_iv
import java.io.ByteArrayOutputStream

class FaceActivity : AppCompatActivity(), FrameProcessor {
    private var activeCamera: Camera = Camera.Back
    private var flashOn = false
    private var faceDetector: FirebaseVisionFaceDetector? = null

    private val options by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
            .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setTrackingEnabled(true)
            .build()
    }

    private val fotoapparat by lazy {
        Fotoapparat.with(applicationContext)
            .into(camera_view)
            .frameProcessor(this)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)
        setSupportActionBar(face_appbar)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        faceDetector?.close()
        super.onStop()
        fotoapparat.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    @Suppress("UNUSED_PARAMETER")
    fun switchCamera(v: View) {
        activeCamera = when (activeCamera) {
            Camera.Front -> Camera.Back
            Camera.Back -> Camera.Front
        }

        fotoapparat.switchTo(
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration
        )
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

    private fun process(target: Bitmap) {
        val firebaseVisionBitmap = FirebaseVisionImage.fromBitmap(target)
        faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        faceDetector.process(firebaseVisionBitmap)
    }

    private fun FirebaseVisionFaceDetector?.process(firebaseVisionBitmap: FirebaseVisionImage) {
        debugSnapshot(firebaseVisionBitmap)

        this?.let {
            detectInImage(firebaseVisionBitmap)
                .addOnSuccessListener(this@FaceActivity::process)
                .addOnFailureListener { e ->
                    Log.d(TAG, e.message)
                }
        }
    }

    @Suppress("ConstantConditionIf")
    private fun debugSnapshot(firebaseVisionBitmap: FirebaseVisionImage) {
        if (DEBUG) {
            runOnUiThread {
                snapshot_iv.setImageBitmap(firebaseVisionBitmap.bitmapForDebugging)
            }
        } else {
            snapshot_iv.visibility = View.GONE
        }
    }

    private fun process(faces: List<FirebaseVisionFace>) {
        Log.d(TAG, "faces: $faces")
        faces.forEach { face ->
            Log.d(TAG, "face: $face")

            val bounds = face.boundingBox
            val rotY = face.headEulerAngleY
            val rotZ = face.headEulerAngleZ

            face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)?.let { leftEar ->
                Log.d(TAG, " leftEar.position = ${leftEar.position}")
            }

            face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)?.let { rightEar ->
                Log.d(TAG, " leftEar.position = ${rightEar.position}")
            }

            if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                Log.d(TAG, "face.smilingProbability = ${face.smilingProbability}")
            }

            if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                Log.d(TAG, "face.rightEyeOpenProbability = ${face.rightEyeOpenProbability}")
            }

            if (face.leftEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                Log.d(TAG, "face.leftEyeOpenProbability = ${face.leftEyeOpenProbability}")
            }
        }
    }

    override fun process(frame: Frame) {
        Log.d(TAG, "frame: ${frame.image.size}, ${frame.size}")

        YuvImage(
            frame.image,
            ImageFormat.NV21,
            frame.size.width,
            frame.size.height,
            null
        ).let { yuvImage ->
            ByteArrayOutputStream().use { output ->
                yuvImage.compressToJpeg(
                    Rect(0, 0, frame.size.width, frame.size.height),
                    100,
                    output
                )
                output.toByteArray().apply {
                    BitmapFactory.decodeByteArray(this, 0, size)?.let { bitmap ->
                        process(bitmap.rotate(-45f).flip(false, true))
                        bitmap.recycle()
                    }
                }
            }
        }
    }

    private fun Bitmap.flip(horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createScaledBitmap(this, width, height, true)
    }

    companion object {
        private const val DEBUG = true
        private const val TAG = "mlkit-face"
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, FaceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}