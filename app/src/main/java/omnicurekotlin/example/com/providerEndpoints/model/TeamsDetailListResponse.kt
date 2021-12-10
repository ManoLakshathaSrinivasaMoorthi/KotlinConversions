package omnicurekotlin.example.com.providerEndpoints.model

class TeamsDetailListResponse {

     var status: Boolean? = null
     var errorId: Double? = null
     var errorMessage: String? = null
     var teamDetails: TeamMemberResponse? = null

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean?) {
        this.status = status
    }

    @JvmName("getErrorId1")
    fun getErrorId(): Double? {
        return errorId
    }

    @JvmName("setErrorId1")
    fun setErrorId(errorId: Double?) {
        this.errorId = errorId
    }

    @JvmName("getTeamDetails1")
    fun getTeamDetails(): TeamMemberResponse? {
        return teamDetails
    }

    @JvmName("setTeamDetails1")
    fun setTeamDetails(teamDetails: TeamMemberResponse?) {
        this.teamDetails = teamDetails
    }

    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }

    @JvmName("setErrorMessage1")
    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }
}
