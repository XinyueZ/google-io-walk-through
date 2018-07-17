package com.demo.slice.api

import com.demo.slice.domain.ProductsData
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductsApi {
    @GET("products?pid=uid4100-40207790-50&fts=suit+dress")
    fun getArticles(@Query("offset") offset: Int): Deferred<ProductsData>


    companion object {
        lateinit var service: ProductsApi
    }
}