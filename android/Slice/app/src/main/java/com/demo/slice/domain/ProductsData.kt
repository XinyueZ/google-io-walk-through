package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class ProductsData(
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("products")
    val products: List<ProductsItem>?
)