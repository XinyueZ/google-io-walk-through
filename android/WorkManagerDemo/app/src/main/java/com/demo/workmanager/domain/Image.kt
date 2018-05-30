package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class Image(@SerializedName("sizes")
                 val sizes: Sizes,
                 @SerializedName("id")
                 val id: String = "")