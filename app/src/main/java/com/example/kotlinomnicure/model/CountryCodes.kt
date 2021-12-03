package com.example.kotlinomnicure.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryCodes {

  constructor() {}

    @Expose
    @SerializedName("countryCodeResponseList")
    private var countryCodeResponseList: List<CountryCodeResponseList?>? = null

    @Expose
    @SerializedName("errorId")
    private var errorId = 0

    @Expose
    @SerializedName("status")
    private var status = false

    fun getCountryCodeResponseList(): List<CountryCodeResponseList?>? {
        return countryCodeResponseList
    }

    fun setCountryCodeResponseList(countryCodeResponseList: List<CountryCodeResponseList?>?) {
        this.countryCodeResponseList = countryCodeResponseList
    }

    fun getErrorId(): Int {
        return errorId
    }

    fun setErrorId(errorId: Int) {
        this.errorId = errorId
    }

    fun getStatus(): Boolean {
        return status
    }

    fun setStatus(status: Boolean) {
        this.status = status
    }

    class CountryCodeResponseList {
        @Expose
        @SerializedName("code")
        var code: String? = null

        @Expose
        @SerializedName("name")
        var name: String? = null

        @Expose
        @SerializedName("id")
        private var id = 0
        fun getId(): String {
            return id.toString()
        }

        fun setId(id: Int) {
            this.id = id
        }
    }
}
