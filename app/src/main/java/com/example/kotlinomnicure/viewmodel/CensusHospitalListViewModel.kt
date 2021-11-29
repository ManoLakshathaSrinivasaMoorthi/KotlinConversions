package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse

class CensusHospitalListViewModel: ViewModel() {

    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null

    fun getHospitalList(id: Long): LiveData<HospitalListResponse>? {
        hospitalListObservable = MutableLiveData<HospitalListResponse>()
      //  getHospitals(id)
        return hospitalListObservable
    }

   /* private fun getHospitals(id: Long) {
        val response = Repo().getHospitalList(id)
        response.enqueue(object :
            Callback<HospitalListResponse> {
            override fun onFailure(call: Call<HospitalListResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<HospitalListResponse>, response: Response<HospitalListResponse>) {
                Handler(Looper.getMainLooper()).post {

                    if (hospitalListObservable== null) {
                        hospitalListObservable = MutableLiveData()
                    }

                    hospitalListObservable?.value
                    Log.e("Response", response.toString())
                }
            }

        })
    }*/

    override fun onCleared() {
        super.onCleared()
        hospitalListObservable = null
    }
}
