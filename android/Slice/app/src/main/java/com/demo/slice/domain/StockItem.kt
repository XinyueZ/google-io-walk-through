package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class StockItem(@SerializedName("color")
                     val color: Color,
                     @SerializedName("size")
                     val size: Size)