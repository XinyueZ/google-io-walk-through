package com.github.xinyuez.mlkit.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.camerakit.CameraKit.FLASH_OFF
import com.camerakit.CameraKit.FLASH_ON
import com.camerakit.CameraKitView
import com.camerakit.CameraKitView.FrameCallback
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.activity_face.face_appbar
import kotlinx.android.synthetic.main.content_face.camera

class FaceActivity : AppCompatActivity(), FrameCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)
        setSupportActionBar(face_appbar)
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
        camera.captureFrame(this)
    }

    override fun onPause() {
        camera.captureFrame(null)
        camera.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    @Suppress("UNUSED_PARAMETER")
    fun switchCamera(v: View) {
        camera.toggleFacing()
    }

    @Suppress("UNUSED_PARAMETER")
    fun flash(v: View) {
        camera.flash = when (camera.flash) {
            FLASH_OFF -> FLASH_ON
            else -> FLASH_OFF
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onFrame(cv: CameraKitView?, bytes: ByteArray?) {
        Log.d(TAG, "ByteArray: ${bytes?.size}")
    }

    companion object {
        internal const val TAG = "face"
        internal fun showInstance(cxt: Activity) {
            val intent = Intent(cxt, FaceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY)
        }
    }
}