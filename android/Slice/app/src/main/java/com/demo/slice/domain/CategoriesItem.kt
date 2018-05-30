package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class CategoriesItem(@SerializedName("localizedId")
                          val localizedId: String = "",
                          @SerializedName("name")
                          val name: String = "",
                          @SerializedName("fullName")
                          val fullName: String = "",
                          @SerializedName("id")
                          val id: String = "",
                          @SerializedName("shortName")
                          val shortName: String = "")