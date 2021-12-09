package omnicurekotlin.example.com.userEndpoints.model

import kotlin.Unit

class CommonResponse {

    var activeList: List<Patient?>? = null
    var dischargedCount: Int? = null
    var dischargedList: List<Patient?>? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var feedbackForm: String? = null
    var tutorial_url: String? = null
    var title: String? = null
    var id: Long? = null
    var pendingList: List<Patient?>? = null
    val encryptedValue: String? = null
    var user: User? = null
    var providerList: List<Provider?>? = null
    var room: Room? = null
    var status: Boolean? = null
    var idToken: String? = null
    var refreshToken: String? = null
    var email: String? = null
    var unit: Unit? = null
    var ward: Ward? = null
    var provider: Provider? = null

    @JvmName("getIdToken1")
    fun getIdToken(): String? {
        return idToken
    }

    @JvmName("setIdToken1")
    fun setIdToken(idToken: String?) {
        this.idToken = idToken
    }

    @JvmName("getRefreshToken1")
    fun getRefreshToken(): String? {
        return refreshToken
    }

    @JvmName("setRefreshToken1")
    fun setRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }


    @JvmName("getFeedbackForm1")
    fun getFeedbackForm(): String? {
        return feedbackForm
    }

    @JvmName("setFeedbackForm1")
    fun setFeedbackForm(feedbackForm: String?) {
        this.feedbackForm = feedbackForm
    }

    @JvmName("getTutorial_url1")
    fun getTutorial_url(): String? {
        return tutorial_url
    }

    @JvmName("setTutorial_url1")
    fun setTutorial_url(tutorial_url: String?) {
        this.tutorial_url = tutorial_url
    }

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    @JvmName("setEmail1")
    fun setEmail(email: String?) {
        this.email = email
    }


    @JvmName("getActiveList1")
    fun getActiveList(): List<Patient?>? {
        return activeList
    }


    fun setActiveList(activeList: List<Patient?>?): CommonResponse {
        this.activeList = activeList
        return this
    }

    @JvmName("getProvider1")
    fun getProvider(): Provider? {
        return provider
    }

    @JvmName("setProvider1")
    fun setProvider(provider: Provider?) {
        this.provider = provider
    }


    @JvmName("getDischargedCount1")
    fun getDischargedCount(): Int? {
        return dischargedCount
    }


    fun setDischargedCount(dischargedCount: Int?): CommonResponse {
        this.dischargedCount = dischargedCount
        return this
    }


    @JvmName("getDischargedList1")
    fun getDischargedList(): List<Patient?>? {
        return dischargedList
    }


    fun setDischargedList(dischargedList: List<Patient?>?): CommonResponse {
        this.dischargedList = dischargedList
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): CommonResponse {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): CommonResponse {
        this.errorMessage = errorMessage
        return this
    }

    @JvmName("getTitle1")
    fun getTitle(): String? {
        return title
    }

    @JvmName("setTitle1")
    fun setTitle(title: String?) {
        this.title = title
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): CommonResponse {
        this.id = id
        return this
    }


    @JvmName("getPendingList1")
    fun getPendingList(): List<Patient?>? {
        return pendingList
    }


    fun setPendingList(pendingList: List<Patient?>?): CommonResponse {
        this.pendingList = pendingList
        return this
    }


    @JvmName("getUser1")
    fun getUser(): User? {
        return user
    }


    fun setUser(provider: User?): CommonResponse {
        user = provider
        return this
    }


    @JvmName("getProviderList1")
    fun getProviderList(): List<Provider?>? {
        return providerList
    }


    fun setProviderList(providerList: List<Provider?>?): CommonResponse {
        this.providerList = providerList
        return this
    }


    @JvmName("getRoom1")
    fun getRoom(): Room? {
        return room
    }


    fun setRoom(room: Room?): CommonResponse {
        this.room = room
        return this
    }



    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): CommonResponse {
        this.status = status
        return this
    }


    @JvmName("getUnit1")
    fun getUnit(): Unit? {
        return unit
    }


    fun setUnit(unit: Unit?): CommonResponse {
        this.unit = unit
        return this
    }


    @JvmName("getWard1")
    fun getWard(): Ward? {
        return ward
    }


    fun setWard(ward: Ward?): CommonResponse {
        this.ward = ward
        return this
    }


    @JvmName("getEncryptedValue1")
    fun getEncryptedValue(): String? {
        return encryptedValue
    }

}
