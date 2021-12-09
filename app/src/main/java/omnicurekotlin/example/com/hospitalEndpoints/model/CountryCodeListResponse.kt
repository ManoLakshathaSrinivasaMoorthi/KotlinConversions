package omnicurekotlin.example.com.hospitalEndpoints.model

class CountryCodeListResponse {

    var status: Boolean? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var countryCodeResponseList: List<CountryCodeList?>? = null

    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }

    @JvmName("setErrorMessage1")
    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean?) {
        this.status = status
    }

    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }

    @JvmName("setErrorId1")
    fun setErrorId(errorId: Int?) {
        this.errorId = errorId
    }

    @JvmName("getCountryCodeResponseList1")
    fun getCountryCodeResponseList(): List<CountryCodeList?>? {
        return countryCodeResponseList
    }

    @JvmName("setCountryCodeResponseList1")
    fun setCountryCodeResponseList(countryCodeResponseList: List<CountryCodeList?>?) {
        this.countryCodeResponseList = countryCodeResponseList
    }
}

