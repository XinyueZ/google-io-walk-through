package com.demo.slice

import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.slice.core.SliceHints
import com.bumptech.glide.Glide
import com.demo.slice.domain.net.provideProductsApiService
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext

class DemoSliceProvider : SliceProvider() {

    private fun createActivityAction(intent: Intent): SliceAction {
        with(intent) {
            return SliceAction.create(
                PendingIntent.getActivity(context, 0, this, 0),
                IconCompat.createWithResource(context, R.drawable.ic_slideshow),
                ListBuilder.SMALL_IMAGE,
                "Enter app"
            )
        }
    }

    override fun onCreateSliceProvider(): Boolean = true

    override fun onBindSlice(sliceUri: Uri): Slice {
        val activityAction = createActivityAction(Intent(context, MainActivity::class.java))
        return when (sliceUri.path) {
            "/hello" -> sliceHelloWorld(sliceUri, activityAction)
            "/grid" -> sliceGrid(sliceUri, activityAction)
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

    private fun sliceGrid(
        sliceUri: Uri,
        activityAction: SliceAction
    ): Slice {
        return runBlocking {
            provideProductsApiService().getArticles(10).await().run {
                list(context, sliceUri, ListBuilder.INFINITY) {
                    header {
                        title = "Fashion"
                        summary = "Show some cloths here..."
                    }
                    gridRow {
                        products?.map { domain ->
                            withContext(DefaultDispatcher) {
                                Glide.with(context).asBitmap().load(domain.image.sizes.best!!.url)
                                    .submit().get()
                            }.run {
                                DomainItem(this, domain.name, domain.priceLabel, domain.clickUrl)
                            }
                        }?.forEach { domainItem ->
                            cell {
                                addImage(
                                    IconCompat.createWithBitmap(
                                        domainItem.bitmap
                                    ), SliceHints.SMALL_IMAGE
                                )
                                contentIntent = PendingIntent.getActivity(
                                    context,
                                    0,
                                    Intent(ACTION_VIEW).apply {
                                        data = Uri.parse(domainItem.clickUrl)
                                    },
                                    0
                                )
                                addText(domainItem.text)
                            }
                            primaryAction = activityAction
                        }
                    }
                }
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

class DomainItem(val bitmap: Bitmap, val title: String, val text: String, val clickUrl: String)