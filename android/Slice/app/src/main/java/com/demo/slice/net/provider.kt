package com.demo.slice.domain.net

import com.demo.slice.api.ProductsApi
import com.google.gson.GsonBuilder
import com.grapesnberries.curllogger.CurlLoggerInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun OkHttpClient.Builder.addDebugInterceptors(): OkHttpClient.Builder {
    addInterceptor(CurlLoggerInterceptor("#!#!"))
    return this
}

private val retrofitBuilder: Retrofit.Builder by lazy {
    Retrofit.Builder()
        .client(
            OkHttpClient.Builder().addDebugInterceptors().build()
        )
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .create()
            )
        )
}

fun provideProductsApiService() =
    retrofitBuilder
        .baseUrl("https://api.shopstyle.com/api/v2/")
        .build()
        .create(ProductsApi::class.java)