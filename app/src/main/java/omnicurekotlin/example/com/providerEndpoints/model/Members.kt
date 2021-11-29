package omnicurekotlin.example.com.providerEndpoints.model

class Members: Comparable<Members> {
    override fun compareTo(other: Members): Int {
        return if (`other` == null) {
            0
        } else Integer.compare(getOrderId(), `other`.getOrderId())
    }
    private var id: String? = null
    private var providerId: String? = null
    private var providerName: String? = null
    private var teamId: String? = null
    private var teamName: String? = null
    private var status: String? = null
    private var rpType: String? = null
    private var orderId = 0
    private var createdTime: String? = null

    private var profilePic: String? = null

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

    fun getTeamId(): String? {
        return teamId
    }

    fun setTeamId(teamId: String?) {
        this.teamId = teamId
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


    fun getOrderId(): Int {
        return orderId
    }

    fun setOrderId(orderId: Int) {
        this.orderId = orderId
    }

    fun getCreatedTime(): String? {
        return createdTime
    }

    fun setCreatedTime(createdTime: String?) {
        this.createdTime = createdTime
    }

    fun getProfilePic(): String? {
        return profilePic
    }

    fun setProfilePic(profilePic: String?) {
        this.profilePic = profilePic
    }


}
