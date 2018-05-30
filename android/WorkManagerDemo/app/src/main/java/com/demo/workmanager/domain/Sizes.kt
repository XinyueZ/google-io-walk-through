package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class Sizes(
    @SerializedName("Best")
    val best: Best?,

    @SerializedName("IPhone")
    val iphone: IPhone?,

    @SerializedName("IPhoneSmall")
    val iPhoneSmall: IPhoneSmall?,

    @SerializedName("Original")
    val original: Original?,

    @SerializedName("Large")
    val large: Large?,

    @SerializedName("Medium")
    val medium: Medium?,
    @SerializedName("XLarge")
    val xlarge: XLarge?,
    @SerializedName("Small")
    val small: Small?

)