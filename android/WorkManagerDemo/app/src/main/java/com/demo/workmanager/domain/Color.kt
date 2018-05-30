package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class Color(@SerializedName("name")
                 val name: String = "")