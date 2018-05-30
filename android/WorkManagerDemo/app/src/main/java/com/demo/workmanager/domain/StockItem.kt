package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class StockItem(@SerializedName("color")
                     val color: Color,
                     @SerializedName("size")
                     val size: Size)