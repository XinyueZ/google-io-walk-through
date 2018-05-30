package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class PromotionalDeal(@SerializedName("retailer")
                           val retailer: Retailer,
                           @SerializedName("endDate")
                           val endDate: EndDate,
                           @SerializedName("typeLabel")
                           val typeLabel: String = "",
                           @SerializedName("id")
                           val id: Int = 0,
                           @SerializedName("shortTitle")
                           val shortTitle: String = "",
                           @SerializedName("type")
                           val type: String = "",
                           @SerializedName("title")
                           val title: String = "",
                           @SerializedName("startDate")
                           val startDate: StartDate)