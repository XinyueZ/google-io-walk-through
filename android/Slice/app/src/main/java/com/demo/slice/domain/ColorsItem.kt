package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class ColorsItem(
    @SerializedName("image")
    val image: Image,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("canonicalColors")
    val canonicalColors: List<CanonicalColorsItem>?
)