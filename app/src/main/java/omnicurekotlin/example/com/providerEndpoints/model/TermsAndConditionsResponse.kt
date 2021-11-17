package omnicurekotlin.example.com.providerEndpoints.model

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




}
