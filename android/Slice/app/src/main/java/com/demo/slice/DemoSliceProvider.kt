package com.demo.slice

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.cell
import androidx.slice.builders.gridRow
import androidx.slice.builders.header
import androidx.slice.builders.list
import androidx.slice.builders.row

class DemoSliceProvider : SliceProvider() {

    private fun createActivityAction(pending: PendingIntent): SliceAction {
        with(pending) {
            return SliceAction.create(
                this,
                IconCompat.createWithResource(context, R.drawable.ic_slideshow),
                ListBuilder.SMALL_IMAGE,
                "Enter app"
            )
        }
    }

    private fun openAppIntent() =
        PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)

    private fun openWebIntent(url: String) = PendingIntent.getActivity(
        context,
        0,
        Intent(ACTION_VIEW).apply {
            data = Uri.parse(url)
        },
        0
    )

    override fun onMapIntentToUri(intent: Intent?): Uri = DemoSliceProvider.getUri(context, "hello")

    override fun onCreateSliceProvider(): Boolean = true

    override fun onBindSlice(sliceUri: Uri): Slice? {
        val openAppAction = createActivityAction(openAppIntent())
        return when (sliceUri.path) {
            "/hello" -> sliceHelloWorld(sliceUri, openAppAction)
            "/list" -> sliceList(sliceUri, openAppAction, setTitleItem = false)
            "/title-item" -> sliceList(sliceUri, openAppAction, setTitleItem = true)
            "/grid" -> sliceGrid(sliceUri, openAppAction)
            "/load-data" -> {
                // TODO Handle [contentResolver.notifyChange] from [DemoService]
                Log.d(TAG, "/load-data")
                null
            }
            else -> sliceNothing(sliceUri, openAppAction)
        }
    }

    // ----------------------------------------------------------------------------------------------
    // Define different slices
    // ----------------------------------------------------------------------------------------------

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

    private fun sliceList(
        sliceUri: Uri,
        activityAction: SliceAction,
        setTitleItem: Boolean
    ): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                title = "Fashion"
                summary = "Show some cloths here..."
                primaryAction = activityAction
            }
            DataSource?.let { itemList ->
                itemList.forEach { domainItem ->
                    if (setTitleItem) {
                        row {
                            setTitleItem(
                                IconCompat.createWithBitmap(
                                    domainItem.bitmap
                                ), ListBuilder.SMALL_IMAGE
                            )
                            primaryAction =
                                    createActivityAction(openWebIntent(domainItem.clickUrl))
                            title = domainItem.title
                            subtitle = domainItem.text
                            contentDescription = domainItem.description
                        }
                    } else {
                        row {
                            primaryAction =
                                    createActivityAction(openWebIntent(domainItem.clickUrl))
                            title = domainItem.title
                            subtitle = domainItem.text
                            contentDescription = domainItem.description
                            addEndItem(
                                IconCompat.createWithBitmap(
                                    domainItem.bitmap
                                ), ListBuilder.SMALL_IMAGE
                            )
                        }
                    }
                }
            } ?: kotlin.run { DemoService.start(context) }
        }
    }

    private fun sliceGrid(
        sliceUri: Uri,
        activityAction: SliceAction
    ): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                title = "Fashion"
                summary = "Show some cloths here..."
                primaryAction = activityAction
            }
            gridRow {
                DataSource?.let { itemList ->
                    itemList.forEach { domainItem ->
                        cell {
                            addImage(
                                IconCompat.createWithBitmap(
                                    domainItem.bitmap
                                ), ListBuilder.SMALL_IMAGE
                            )
                            contentIntent = openWebIntent(domainItem.clickUrl)
                            addText(domainItem.text)
                        }
                        primaryAction = activityAction
                    }
                } ?: kotlin.run { DemoService.start(context) }
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

    companion object {
        private val TAG: String by lazy { DemoSliceProvider::class.java.name }

        fun getUri(context: Context, path: String): Uri {
            return Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(context.packageName)
                .appendPath(path)
                .build()
        }

        internal var DataSource: List<DomainItem>? = null
    }
}

class DomainItem(
    val bitmap: Bitmap,
    val title: String,
    val text: String,
    val clickUrl: String,
    val description: String = ""
)