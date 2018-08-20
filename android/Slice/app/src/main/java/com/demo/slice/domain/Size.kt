package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class Size(
    @SerializedName("name")
    val name: String = ""
)