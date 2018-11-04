package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.xinyuez.mlkit.demo.Camera.Front
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor
import io.fotoapparat.selector.firstAvailable
import io.fotoapparat.selector.front
import io.fotoapparat.selector.highestSensorSensitivity
import io.fotoapparat.selector.off
import io.fotoapparat.selector.torch
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.activity_face.function_toggle
import kotlinx.android.synthetic.main.content_face.camera_view
import kotlinx.android.synthetic.main.content_face.msg_tv
import kotlinx.android.synthetic.main.content_face.overlay
import kotlinx.android.synthetic.main.content_face.snapshot_iv
import java.io.ByteArrayOutputStream

class FaceActivity : AppCompatActivity(), FrameProcessor {
    private var activeCamera: Camera = Camera.Front
    private var flashOn = false
    private var faceDetector: FirebaseVisionFaceDetector? = null

    private val options by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .enableTracking()
            .build()
    }

    private val fotoapparat by lazy {
        Fotoapparat.with(applicationContext)
            .into(camera_view)
            .lensPosition(front())
            .sensorSensitivity(highestSensorSensitivity())
            .frameProcessor(this)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)
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
        flashOn = false
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

    private fun process(target: ByteArray, width: Int, height: Int, rotation: Int) {
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(width)
            .setHeight(height)
            .setRotation(rotation)
            .build()

        val firebaseVisionBitmap = FirebaseVisionImage.fromByteArray(target, metadata)
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
                snapshot_iv.visibility = View.VISIBLE
                snapshot_iv.setImageBitmap(firebaseVisionBitmap.bitmapForDebugging)
            }
        }
    }

    private fun process(faces: List<FirebaseVisionFace>) {
        Log.d(TAG, "faces: $faces")
        overlay.clear()

        faces.forEach { face ->
            Log.d(TAG, "face: $face")

            with(FaceGraphic(overlay)) {
                overlay.add(this)
                updateFace(face, activeCamera)
            }

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

        when {
            function_toggle.isChecked -> {
                process(
                    frame.image,
                    frame.size.width,
                    frame.size.height,
                    FirebaseVisionImageMetadata.ROTATION_270
                )
                runOnUiThread {
                    msg_tv.text = getString(R.string.process_from_bytes_directly)
                }
            }
            else -> {
                processFromBytesToBitmap(frame)
                runOnUiThread {
                    msg_tv.text = getString(R.string.process_from_bytes_to_bitmap)
                }
            }
        }
    }

    /**
     * Convert bytes to bitmap and process prediction.
     */
    private fun processFromBytesToBitmap(frame: Frame) {
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
                        process(
                            bitmap.rotate(90f).flip(
                                false, activeCamera == Front
                            )
                        )
                        bitmap.recycle()
                    }
                }
            }
        }
    }

    companion object {
        private const val DEBUG = false
        private const val TAG = "mlkit-face"
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, FaceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }

        private fun Bitmap.flip(horizontal: Boolean, vertical: Boolean): Bitmap {
            val matrix = Matrix()
            matrix.preScale(
                (if (horizontal) -1 else 1).toFloat(),
                (if (vertical) -1 else 1).toFloat()
            )
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }

        private fun Bitmap.rotate(degrees: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)
            return Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
        }
    }
}

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 *
 * See. https://github.com/firebase/quickstart-android/blob/master/mlkit/app/src/main/java/com/google/firebase/samples/apps/mlkit/kotlin/facedetection/FaceGraphic.kt
 */
private class FaceGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {
    private var activeCamera: Camera? = null

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    @Volatile
    private lateinit var firebaseVisionFace: FirebaseVisionFace

    init {

        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[currentColorIndex]

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    /**
     * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
     * portions of the overlay to trigger a redraw.
     */
    fun updateFace(face: FirebaseVisionFace, facing: Camera) {
        firebaseVisionFace = face
        this.activeCamera = facing
        postInvalidate()
    }

    /** Draws the face annotations for position on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace

        // Draws a circle at the position of the detected face, with the face's track id below.
        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)
        canvas.drawText("id: ${face.trackingId}", x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint)
        canvas.drawText(
            "happiness: ${String.format("%.2f", face.smilingProbability)}",
            x + ID_X_OFFSET * 3,
            y - ID_Y_OFFSET,
            idPaint
        )
        if (activeCamera == Camera.Front) {
            canvas.drawText(
                "right eye: ${String.format("%.2f", face.rightEyeOpenProbability)}",
                x - ID_X_OFFSET,
                y,
                idPaint
            )
            canvas.drawText(
                "left eye: ${String.format("%.2f", face.leftEyeOpenProbability)}",
                x + ID_X_OFFSET * 6,
                y,
                idPaint
            )
        } else {
            canvas.drawText(
                "left eye: ${String.format("%.2f", face.leftEyeOpenProbability)}",
                x - ID_X_OFFSET,
                y,
                idPaint
            )
            canvas.drawText(
                "right eye: ${String.format("%.2f", face.rightEyeOpenProbability)}",
                x + ID_X_OFFSET * 6,
                y,
                idPaint
            )
        }

        // Draws a bounding box around the face.
        val xOffset = scaleX(face.boundingBox.width() / 2.0f)
        val yOffset = scaleY(face.boundingBox.height() / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        canvas.drawRect(left, top, right, bottom, boxPaint)

        // draw landmarks
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_BOTTOM)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_CHEEK)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EAR)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_LEFT)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EYE)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.NOSE_BASE)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_CHEEK)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EAR)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EYE)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_RIGHT)
    }

    private fun drawLandmarkPosition(canvas: Canvas, face: FirebaseVisionFace, landmarkID: Int) {
        val landmark = face.getLandmark(landmarkID)
        landmark?.let {
            val point = landmark.position
            canvas.drawCircle(
                translateX(point.x),
                translateY(point.y),
                FACE_POSITION_RADIUS, idPaint
            )
        }
    }

    companion object {
        private const val FACE_POSITION_RADIUS = 10.0f
        private const val ID_TEXT_SIZE = 40.0f
        private const val ID_Y_OFFSET = 50.0f
        private const val ID_X_OFFSET = -50.0f
        private const val BOX_STROKE_WIDTH = 5.0f

        private val COLOR_CHOICES = intArrayOf(
            Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA,
            Color.RED, Color.WHITE, Color.YELLOW
        )
        private var currentColorIndex = 0
    }
}