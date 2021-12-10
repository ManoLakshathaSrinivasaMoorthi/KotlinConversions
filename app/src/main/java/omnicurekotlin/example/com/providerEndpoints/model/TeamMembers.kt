package omnicurekotlin.example.com.providerEndpoints.model

class TeamMembers {

    var id: String? = null
    var providerId: String? = null
    var providerName: String? = null
    var teamName: String? = null
    var status: String? = null
    var rpType: String? = null
    var createdTime: String? = null


    @JvmName("getId1")
    fun getId(): String? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: String?) {
        this.id = id
    }

    @JvmName("getProviderId1")
    fun getProviderId(): String? {
        return providerId
    }

    @JvmName("setProviderId1")
    fun setProviderId(providerId: String?) {
        this.providerId = providerId
    }

    @JvmName("getProviderName1")
    fun getProviderName(): String? {
        return providerName
    }

    @JvmName("setProviderName1")
    fun setProviderName(providerName: String?) {
        this.providerName = providerName
    }

    @JvmName("getTeamName1")
    fun getTeamName(): String? {
        return teamName
    }

    @JvmName("setTeamName1")
    fun setTeamName(teamName: String?) {
        this.teamName = teamName
    }

    @JvmName("getStatus1")
    fun getStatus(): String? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: String?) {
        this.status = status
    }

    @JvmName("getRpType1")
    fun getRpType(): String? {
        return rpType
    }

    @JvmName("setRpType1")
    fun setRpType(rpType: String?) {
        this.rpType = rpType
    }

    @JvmName("getCreatedTime1")
    fun getCreatedTime(): String? {
        return createdTime
    }

    @JvmName("setCreatedTime1")
    fun setCreatedTime(createdTime: String?) {
        this.createdTime = createdTime
    }


}
