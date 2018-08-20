package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class CanonicalColorsItem(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("id")
    val id: String = ""
)