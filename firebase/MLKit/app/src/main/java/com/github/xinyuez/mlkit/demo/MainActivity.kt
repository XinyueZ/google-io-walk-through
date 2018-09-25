package com.github.xinyuez.mlkit.demo

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.clearFindViewByIdCache
import kotlinx.android.synthetic.main.activity_main.main_appbar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_appbar)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFindViewByIdCache()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openOCR(v: View) {
        OCRActivity.showInstance(this)
    }

    @Suppress("UNUSED_PARAMETER")
    fun openFace(v: View) {
        openFaceWithPermissions()
    }

    @AfterPermissionGranted(PERMISSION)
    private fun openFaceWithPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            FaceActivity.showInstance(this)
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.camera_rationale),
                    PERMISSION,
                    Manifest.permission.CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {
        private const val PERMISSION = 234
    }
}
