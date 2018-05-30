package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class CanonicalSize(@SerializedName("name")
                         val name: String = "",
                         @SerializedName("id")
                         val id: String = "")