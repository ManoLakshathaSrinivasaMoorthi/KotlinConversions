package omnicurekotlin.example.com.providerEndpoints.model

class TeamMembers {

    private var id: String? = null
    private var providerId: String? = null
    private var providerName: String? = null
    private var teamName: String? = null
    private var status: String? = null
    private var rpType: String? = null
    private var createdTime: String? = null


    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getProviderId(): String? {
        return providerId
    }

    fun setProviderId(providerId: String?) {
        this.providerId = providerId
    }

    fun getProviderName(): String? {
        return providerName
    }

    fun setProviderName(providerName: String?) {
        this.providerName = providerName
    }

    fun getTeamName(): String? {
        return teamName
    }

    fun setTeamName(teamName: String?) {
        this.teamName = teamName
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getRpType(): String? {
        return rpType
    }

    fun setRpType(rpType: String?) {
        this.rpType = rpType
    }

    fun getCreatedTime(): String? {
        return createdTime
    }

    fun setCreatedTime(createdTime: String?) {
        this.createdTime = createdTime
    }


}
