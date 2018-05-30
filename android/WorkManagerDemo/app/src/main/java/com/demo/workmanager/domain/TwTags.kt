package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class TwTags(@SerializedName("adScope")
                  val adScope: String = "",
                  @SerializedName("twScope")
                  val twScope: String = "",
                  @SerializedName("twApplied")
                  val twApplied: Boolean = false,
                  @SerializedName("twId")
                  val twId: String = "",
                  @SerializedName("useTw")
                  val useTw: Boolean = false)