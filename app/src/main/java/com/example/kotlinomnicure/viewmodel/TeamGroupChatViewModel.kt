package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.TeamDetailsByNameRequestBody
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamGroupChatViewModel: ViewModel() {
    private val TAG = javaClass.simpleName


    private var commonResponseMutableLiveData: MutableLiveData<TeamsDetailListResponse?>? = null


    fun getTeamName(patientId: Long, teamName: String): LiveData<TeamsDetailListResponse?>? {
        commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse?>()

        teamGroupNameRetro(patientId, teamName)
        return commonResponseMutableLiveData
    }

    private fun teamGroupNameRetro(patientId: Long, teamName: String) {
        val errMsg = arrayOfNulls<String>(1)


        ApiClient().getApiProviderEndpoints(encrypt = true, decrypt = true)
            ?.teamDetailsByName(TeamDetailsByNameRequestBody(patientId, teamName))
            ?.enqueue(object : Callback<TeamsDetailListResponse?> {
                override fun onResponse(
                    call: Call<TeamsDetailListResponse?>,
                    response: Response<TeamsDetailListResponse?>, ) {

                    if (response.isSuccessful) {
                        val listResponse: TeamsDetailListResponse? = response.body()
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData =
                                MutableLiveData<TeamsDetailListResponse?>()
                        }
                        commonResponseMutableLiveData!!.value = listResponse
                    }
                }

                override fun onFailure(call: Call<TeamsDetailListResponse?>, t: Throwable) {

                    errMsg[0] = Constants.API_ERROR
                    val response = TeamsDetailListResponse()
                    response.setErrorMessage(errMsg[0])
                    if (commonResponseMutableLiveData == null) {
                        commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse?>()
                    }
                    commonResponseMutableLiveData!!.value = response
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        commonResponseMutableLiveData = null
    }
}