package com.hackfest.swiftaid.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hackfest.swiftaid.viewModels.AmbulanceViewModel

class AmbulanceViewModelFactory(private val orgAuthID:String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AmbulanceViewModel::class.java)){
            return AmbulanceViewModel(orgAuthID) as T
        }
        throw java.lang.IllegalArgumentException("Unknown View Model Class")

    }
}
