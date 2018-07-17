package com.demo.slice.domain

import com.google.gson.annotations.SerializedName

data class ProductsItem(@SerializedName("clickUrl")
                        val clickUrl: String = "",
                        @SerializedName("description")
                        val description: String = "",
                        @SerializedName("discount")
                        val discount: Int = 0,
                        @SerializedName("locale")
                        val locale: String = "",
                        @SerializedName("unbrandedName")
                        val unbrandedName: String = "",
                        @SerializedName("colors")
                        val colors: List<ColorsItem>?,
                        @SerializedName("brandedName")
                        val brandedName: String = "",
                        @SerializedName("extractDate")
                        val extractDate: String = "",
                        @SerializedName("sizes")
                        val sizes: List<Sizes>?,
                        @SerializedName("price")
                        val price: Float = 0f,
                        @SerializedName("currency")
                        val currency: String = "",
                        @SerializedName("inStock")
                        val inStock: Boolean = false,
                        @SerializedName("id")
                        val id: Int = 0,
                        @SerializedName("categories")
                        val categories: List<CategoriesItem>?,
                        @SerializedName("stock")
                        val stock: List<StockItem>?,
                        @SerializedName("brand")
                        val brand: Brand,
                        @SerializedName("priceLabel")
                        val priceLabel: String = "",
                        @SerializedName("image")
                        val image: Image,
                        @SerializedName("salePrice")
                        val salePrice: Float = 0f,
                        @SerializedName("retailer")
                        val retailer: Retailer,
                        @SerializedName("salePriceLabel")
                        val salePriceLabel: String = "",
                        @SerializedName("rental")
                        val rental: Boolean = false,
                        @SerializedName("preOwned")
                        val preOwned: Boolean = false,
                        @SerializedName("name")
                        val name: String = "",
                        @SerializedName("seeMoreLabel")
                        val seeMoreLabel: String = "",
                        @SerializedName("promotionalDeal")
                        val promotionalDeal: PromotionalDeal,
                        @SerializedName("lastModified")
                        val lastModified: String = "")