package omnicurekotlin.example.com.userEndpoints.model

import omnicurekotlin.example.com.providerEndpoints.model.TermsConditions

class TermsAndConditionsResponse {

    var status: Boolean? = null
    var errorId: Int? = null
    var errorMessage: String? = null

    @JvmName("getConfiguration1")
    fun getConfiguration(): TermsConditions? {
        return configuration
    }

    @JvmName("setConfiguration1")
    fun setConfiguration(configuration: TermsConditions?) {
        this.configuration = configuration
    }

     var configuration: TermsConditions? = null

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
    @JvmName("setErrorMessage1")
    fun setErrorMessage(errorMessage: String) {
        //errorMessage = errorMessage
    }



}