package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class CanonicalSize(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("id")
    val id: String = ""
)