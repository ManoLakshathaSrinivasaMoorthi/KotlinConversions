package omnicurekotlin.example.com.userEndpoints.model

class CountryCodeListResponse {

    private var status: Boolean? = null
    private var errorId: Int? = null
    var errorMessage: String? = null
    var countryCodeResponseList: List<CountryCodeList?>? = null

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?) {
        this.errorId = errorId
    }

    fun getCountryCodeResponseList(): List<CountryCodeList?>? {
        return countryCodeResponseList
    }

    fun setCountryCodeResponseList(countryCodeResponseList: List<CountryCodeList?>?) {
        this.countryCodeResponseList = countryCodeResponseList
    }

//    @Override
//    public CountryCodeListResponse set(String fieldName, Object value) {
//        return (CountryCodeListResponse) super.set(fieldName, value);
//    }
//
//    @Override
//    public CountryCodeListResponse clone() {
//        return (CountryCodeListResponse) super.clone();
//    }

}
