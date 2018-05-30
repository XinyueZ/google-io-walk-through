package com.demo.workmanager.domain

import com.google.gson.annotations.SerializedName

data class Metadata(@SerializedName("offset")
                    val offset: Int = 0,
                    @SerializedName("freeTextSearches")
                    val freeTextSearches: List<String>?,
                    @SerializedName("showColorFilter")
                    val showColorFilter: Boolean = false,
                    @SerializedName("usedLowLevelGateway")
                    val usedLowLevelGateway: Boolean = false,
                    @SerializedName("majorCategory")
                    val majorCategory: MajorCategory,
                    @SerializedName("total")
                    val total: Int = 0,
                    @SerializedName("showSizeFilter")
                    val showSizeFilter: Boolean = false,
                    @SerializedName("elasticsearchEligible")
                    val elasticsearchEligible: Boolean = false,
                    @SerializedName("showHeelHeightFilter")
                    val showHeelHeightFilter: Boolean = false,
                    @SerializedName("engine")
                    val engine: String = "",
                    @SerializedName("limit")
                    val limit: Int = 0,
                    @SerializedName("showConditionFilter")
                    val showConditionFilter: Boolean = false,
                    @SerializedName("twTags")
                    val twTags: TwTags,
                    @SerializedName("category")
                    val category: Category)