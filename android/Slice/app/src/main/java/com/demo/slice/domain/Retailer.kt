package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class Retailer(
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("deeplinkSupport")
    val deeplinkSupport: Boolean = false,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("hostDomain")
    val hostDomain: String = "",
    @SerializedName("id")
    val id: String = ""
)