package omnicurekotlin.example.com.providerEndpoints.model

class TeamsDetailListResponse {

    private var status: Boolean? = null
    private var errorId: Double? = null
    private var errorMessage: String? = null
    private var teamDetails: TeamMemberResponse? = null

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getErrorId(): Double? {
        return errorId
    }

    fun setErrorId(errorId: Double?) {
        this.errorId = errorId
    }

    fun getTeamDetails(): TeamMemberResponse? {
        return teamDetails
    }

    fun setTeamDetails(teamDetails: TeamMemberResponse?) {
        this.teamDetails = teamDetails
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }
}
