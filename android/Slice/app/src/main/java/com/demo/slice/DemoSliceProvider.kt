package com.demo.slice

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.list
import androidx.slice.builders.row

class DemoSliceProvider : SliceProvider() {

    private fun createActivityAction(): SliceAction {
        with(Intent(context, MainActivity::class.java)) {
            return SliceAction.create(
                PendingIntent.getActivity(context, 0, this, 0),
                IconCompat.createWithResource(context, R.drawable.ic_slideshow),
                ListBuilder.ICON_IMAGE,
                "Enter app"
            )
        }
    }

    override fun onCreateSliceProvider(): Boolean = true

    override fun onBindSlice(sliceUri: Uri): Slice {
        val activityAction = createActivityAction()
        return when (sliceUri.path) {
            "/hello" -> sliceHelloWorld(sliceUri, activityAction)
            else -> sliceNothing(sliceUri, activityAction)
        }
    }

    //----------------------------------------------------------------------------------------------
    // Define different slices
    //----------------------------------------------------------------------------------------------

    private fun sliceHelloWorld(
        sliceUri: Uri,
        activityAction: SliceAction
    ): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                primaryAction = activityAction
                title = "Hello World."
            }
        }
    }

    private fun sliceNothing(
        sliceUri: Uri,
        activityAction: SliceAction
    ): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                primaryAction = activityAction
                title = "URI not recognized."
            }
        }
    }
}