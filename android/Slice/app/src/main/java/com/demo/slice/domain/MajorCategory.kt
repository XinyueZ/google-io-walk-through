package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class MajorCategory(
    @SerializedName("localizedId")
    val localizedId: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("fullName")
    val fullName: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("shortName")
    val shortName: String = ""
)