package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class EndDate(@SerializedName("date")
                   val date: String = "",
                   @SerializedName("friendly")
                   val friendly: String = "",
                   @SerializedName("timestamp")
                   val timestamp: Int = 0)