package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class IPhone(@SerializedName("sizeName")
                  val sizeName: String = "",
                  @SerializedName("actualWidth")
                  val actualWidth: Int = 0,
                  @SerializedName("width")
                  val width: Int = 0,
                  @SerializedName("actualHeight")
                  val actualHeight: Int = 0,
                  @SerializedName("url")
                  val url: String = "",
                  @SerializedName("height")
                  val height: Int = 0)