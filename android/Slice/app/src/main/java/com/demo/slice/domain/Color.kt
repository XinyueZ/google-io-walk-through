package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class Color(@SerializedName("name")
                 val name: String = "")