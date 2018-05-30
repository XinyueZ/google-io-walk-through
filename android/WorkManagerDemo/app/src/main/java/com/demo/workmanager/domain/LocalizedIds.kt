package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class LocalizedIds(@SerializedName("de-DE")
                        val deDE: String = "",
                        @SerializedName("en-US")
                        val enUS: String = "",
                        @SerializedName("en-CA")
                        val enCA: String = "",
                        @SerializedName("en-AU")
                        val enAU: String = "",
                        @SerializedName("fr-FR")
                        val frFR: String = "",
                        @SerializedName("zh-CN")
                        val zhCN: String = "",
                        @SerializedName("en-GB")
                        val enGB: String = "",
                        @SerializedName("ja-JP")
                        val jaJP: String = "")