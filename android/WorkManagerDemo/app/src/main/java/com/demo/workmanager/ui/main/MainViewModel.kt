package com.demo.workmanager.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.workmanager.domain.ProductsData

class MainViewModel : ViewModel() {
    val productData = MutableLiveData<ProductsData>()

}
