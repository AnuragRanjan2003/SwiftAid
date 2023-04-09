package com.hackfest.swiftaid.viewModels.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hackfest.swiftaid.repository.Repository
import com.hackfest.swiftaid.viewModels.AmbulanceMapFragmentViewModel

class AmbulanceFactory(private val repo : Repository,private val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AmbulanceMapFragmentViewModel(repo,application) as T
    }
}
