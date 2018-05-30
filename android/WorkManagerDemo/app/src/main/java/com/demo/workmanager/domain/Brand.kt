package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class Brand(@SerializedName("name")
                 val name: String = "",
                 @SerializedName("id")
                 val id: String = "")