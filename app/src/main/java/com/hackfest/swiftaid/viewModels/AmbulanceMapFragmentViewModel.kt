package com.hackfest.swiftaid.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hackfest.swiftaid.models.LocationLiveData
import com.hackfest.swiftaid.models.Request
import com.hackfest.swiftaid.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AmbulanceMapFragmentViewModel @Inject constructor(private val repo: Repository, app: Application) :
    ViewModel() {
    private val locationLiveData = LocationLiveData(app)
    private val reqId = MutableLiveData<String>()
    private val request: MutableLiveData<Request> by lazy { MutableLiveData<Request>() }

    fun getLocationLiveData() = locationLiveData

    fun startLocationLiveUpdates() {
        locationLiveData.startLocationUpdates()
    }


    fun getAssignedRequest(ambId: String) {
        repo.getAssignedRequest(ambId) {
            it?.let { reqId.value = it }
        }
    }

    fun getRequest(reqId: String) {
        repo.getSingleRequest(reqId) {
            it?.let { request.value = it }
        }
    }

    fun observeReqId(): LiveData<String> = reqId

    fun observeRequest(): LiveData<Request> = request


}
