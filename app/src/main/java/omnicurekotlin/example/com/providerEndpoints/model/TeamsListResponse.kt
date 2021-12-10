package omnicurekotlin.example.com.providerEndpoints.model

class TeamsListResponse {

     var count: Int? = null
     var errorId: Double? = null
     var errorMessage: String? = null
     var status: Boolean? = null
     var id: Long? = null
     var teamDetailsList: List<Teams?>? = null


    @JvmName("getCount1")
    fun getCount(): Int? {
        return count
    }

    @JvmName("setCount1")
    fun setCount(count: Int?) {
        this.count = count
    }

    @JvmName("getErrorId1")
    fun getErrorId(): Double? {
        return errorId
    }

    @JvmName("setErrorId1")
    fun setErrorId(errorId: Double?) {
        this.errorId = errorId
    }

    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }

    @JvmName("setErrorMessage1")
    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: Long?) {
        this.id = id
    }

    @JvmName("getTeamDetailsList1")
    fun getTeamDetailsList(): List<Teams?>? {
        return teamDetailsList
    }

    @JvmName("setTeamDetailsList1")
    fun setTeamDetailsList(teamDetailsList: List<Teams?>?) {
        this.teamDetailsList = teamDetailsList
    }

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean?) {
        this.status = status
    }
}
