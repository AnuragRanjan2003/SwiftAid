package com.hackfest.swiftaid.viewModels.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hackfest.swiftaid.repository.Repository
import com.hackfest.swiftaid.viewModels.MapsViewModel

class MapViewModelFactory(private val repository: Repository, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapsViewModel(application,repository) as T
    }
}