package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class Category(@SerializedName("localizedId")
                    val localizedId: String? = "",
                    @SerializedName("localizedIds")
                    val localizedIds: LocalizedIds?,
                    @SerializedName("name")
                    val name: String? = "",
                    @SerializedName("fullName")
                    val fullName: String? = "",
                    @SerializedName("id")
                    val id: String? = "",
                    @SerializedName("shortName")
                    val shortName: String? = "")