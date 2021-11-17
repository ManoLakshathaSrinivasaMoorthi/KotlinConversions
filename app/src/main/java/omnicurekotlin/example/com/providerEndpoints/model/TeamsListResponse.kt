package omnicurekotlin.example.com.providerEndpoints.model

class TeamsListResponse {

    private var count: Int? = null
    private var errorId: Double? = null
    private var errorMessage: String? = null
    private var status: Boolean? = null
    private var id: Long? = null
    private var teamDetailsList: List<Teams?>? = null


    fun getCount(): Int? {
        return count
    }

    fun setCount(count: Int?) {
        this.count = count
    }

    fun getErrorId(): Double? {
        return errorId
    }

    fun setErrorId(errorId: Double?) {
        this.errorId = errorId
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?) {
        this.id = id
    }

    fun getTeamDetailsList(): List<Teams?>? {
        return teamDetailsList
    }

    fun setTeamDetailsList(teamDetailsList: List<Teams?>?) {
        this.teamDetailsList = teamDetailsList
    }

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }
}
