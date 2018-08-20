package com.demo.slice

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.demo.slice.domain.net.provideProductsApiService
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext

/**
 * [DemoService] starts loading data and trigger on [DemoSliceProvider] after finishing.
 */
class DemoService : IntentService("Demo source provider service") {
    override fun onHandleIntent(intent: Intent?) {
        runBlocking {
            provideProductsApiService().getArticles(10).await().run {
                products?.map { domain ->
                    withContext(DefaultDispatcher) {
                        Glide.with(this@DemoService).asBitmap().load(domain.image.sizes.best!!.url)
                            .submit().get()
                    }.run {
                        DomainItem(
                            this,
                            domain.name,
                            domain.priceLabel,
                            domain.clickUrl,
                            domain.description
                        )
                    }
                }.also { it?.onDataLoad(this@DemoService) }
            }
        }
    }

    companion object {
        private fun List<DomainItem>?.onDataLoad(context: Context) {
            // TODO Use below codes after [SliceProvider.onBindSlice] can handle [contentResolver.notifyChange].
            // val uri = DemoSliceProvider.getUri(context, "load-data")
            // context.applicationContext.contentResolver.notifyChange(uri, null)
            DemoSliceProvider.DataSource = this
        }

        fun start(context: Context) {
            context.applicationContext.startService(Intent(context, DemoService::class.java))
        }
    }
}