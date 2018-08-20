package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("sizes")
    val sizes: Sizes,
    @SerializedName("id")
    val id: String = ""
)