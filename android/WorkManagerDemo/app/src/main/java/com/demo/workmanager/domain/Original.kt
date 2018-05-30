package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class Original(@SerializedName("sizeName")
                    val sizeName: String = "",
                    @SerializedName("actualWidth")
                    val actualWidth: Int = 0,
                    @SerializedName("actualHeight")
                    val actualHeight: Int = 0,
                    @SerializedName("url")
                    val url: String = "")