package com.github.xinyuez.mlkit.demo

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.xinyuez.mlkit.demo.MainActivity.CameraBasedDemo.AUTOML
import com.github.xinyuez.mlkit.demo.MainActivity.CameraBasedDemo.BARCODE
import com.github.xinyuez.mlkit.demo.MainActivity.CameraBasedDemo.FACE
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {
    private var cameraBasedDemo = FACE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_appbar)
    }

    override fun onDestroy() {
        cameraBasedDemo = FACE
        super.onDestroy()
        clearFindViewByIdCache()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openOCR(v: View) {
        OCRActivity.showInstance(this)
    }

    @Suppress("UNUSED_PARAMETER")
    fun openFace(v: View) {
        cameraBasedDemo = FACE
        openCameraBasedDemo()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openBarcode(v: View) {
        cameraBasedDemo = BARCODE
        openCameraBasedDemo()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openAutoML(v: View) {
        cameraBasedDemo = AUTOML
        openCameraBasedDemo()
    }

    @AfterPermissionGranted(PERMISSION)
    private fun openCameraBasedDemo() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            when (cameraBasedDemo) {
                FACE ->
                    FaceActivity.showInstance(this)
                BARCODE ->
                    BarcodeActivity.showInstance(this)
                AUTOML ->
                    AutoMLActivity.showInstance(this)
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.camera_rationale),
                PERMISSION,
                Manifest.permission.CAMERA
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {
        private const val PERMISSION = 234
    }

    enum class CameraBasedDemo {
        FACE, BARCODE, AUTOML
    }
}
