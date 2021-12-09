package omnicurekotlin.example.com.providerEndpoints.model

class Members: Comparable<Members> {
    override fun compareTo(other: Members): Int {
        return if (`other` == null) {
            0
        } else Integer.compare(getOrderId(), `other`.getOrderId())
    }
     var id: String? = null
     var providerId: String? = null
     var providerName: String? = null
     var teamId: String? = null
     var teamName: String? = null
     var status: String? = null
     var rpType: String? = null
     var orderId = 0
     var createdTime: String? = null
     var profilePic: String? = null

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

    @JvmName("getTeamId1")
    fun getTeamId(): String? {
        return teamId
    }

    @JvmName("setTeamId1")
    fun setTeamId(teamId: String?) {
        this.teamId = teamId
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


    @JvmName("getOrderId1")
    fun getOrderId(): Int {
        return orderId
    }

    @JvmName("setOrderId1")
    fun setOrderId(orderId: Int) {
        this.orderId = orderId
    }

    @JvmName("getCreatedTime1")
    fun getCreatedTime(): String? {
        return createdTime
    }

    @JvmName("setCreatedTime1")
    fun setCreatedTime(createdTime: String?) {
        this.createdTime = createdTime
    }

    @JvmName("getProfilePic1")
    fun getProfilePic(): String? {
        return profilePic
    }

    @JvmName("setProfilePic1")
    fun setProfilePic(profilePic: String?) {
        this.profilePic = profilePic
    }


}
