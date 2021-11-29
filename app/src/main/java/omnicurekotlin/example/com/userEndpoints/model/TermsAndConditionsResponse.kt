package omnicurekotlin.example.com.userEndpoints.model

import omnicurekotlin.example.com.providerEndpoints.model.TermsConditions

class TermsAndConditionsResponse {
    private var status: Boolean? = null


    private var errorId: Int? = null

    fun getConfiguration(): TermsConditions? {
        return configuration
    }

    fun setConfiguration(configuration: TermsConditions?) {
        this.configuration = configuration
    }

    private var configuration: TermsConditions? = null

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


   /* operator fun set(fieldName: String?, value: Any?): TermsAndConditionsResponse? {
        return super.set(fieldName, value) as TermsAndConditionsResponse?
    }

    fun clone(): TermsAndConditionsResponse? {
        return super.clone() as TermsAndConditionsResponse?
    }*/

}