package com.demo.workmanager.api

import com.demo.workmanager.domain.ProductsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductsApi {
    @GET("products?pid=uid4100-40207790-50&fts=suit+dress")
    fun getArticles(@Query("offset") offset: Int): Call<ProductsData>


    companion object {
        lateinit var service: ProductsApi
    }
}