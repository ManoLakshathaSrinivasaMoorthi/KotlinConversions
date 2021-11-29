package omnicurekotlin.example.com.userEndpoints.model

import kotlin.Unit

class CommonResponse {

    private var activeList: List<Patient?>? = null
    private var dischargedCount: Int? = null
    private var dischargedList: List<Patient?>? = null
    private var errorId: Int? = null
    var errorMessage: String? = null
    private var feedbackForm: String? = null
    private var tutorial_url: String? = null
    private var title: String? = null
    private var id: Long? = null
    private var pendingList: List<Patient?>? = null

    var user: User? = null

    private var providerList: List<Provider?>? = null

    private var room: Room? = null

    var status: Boolean? = null
    private var idToken: String? = null
    private var refreshToken: String? = null

    private var email: String? = null

    private var unit: Unit? = null
    private var ward: Ward? = null

    private var provider: Provider? = null

    fun getIdToken(): String? {
        return idToken
    }

    fun setIdToken(idToken: String?) {
        this.idToken = idToken
    }

    fun getRefreshToken(): String? {
        return refreshToken
    }

    fun setRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }

    /**
     * The value may be `null`.
     */
    /*  @com.google.api.client.util.Key
  private java.util.List<OtherBspList> otherBspList;

  static {
    // hack to force ProGuard to consider Provider used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(OtherBspList.class);
  }

  public List<OtherBspList> getOtherBspList() {
    return otherBspList;
  }

  public CommonResponse setOtherBspList(List<OtherBspList> otherBspList) {
    this.otherBspList = otherBspList;
    return this;
  }*/
    fun getFeedbackForm(): String? {
        return feedbackForm
    }

    fun setFeedbackForm(feedbackForm: String?) {
        this.feedbackForm = feedbackForm
    }

    fun getTutorial_url(): String? {
        return tutorial_url
    }

    fun setTutorial_url(tutorial_url: String?) {
        this.tutorial_url = tutorial_url
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    /**
     * @return value or `null` for none
     */
    fun getActiveList(): List<Patient?>? {
        return activeList
    }

    /**
     * @param activeList activeList or `null` for none
     */
    fun setActiveList(activeList: List<Patient?>?): CommonResponse? {
        this.activeList = activeList
        return this
    }

    fun getProvider(): Provider? {
        return provider
    }

    fun setProvider(provider: Provider?) {
        this.provider = provider
    }

    /**
     * @return value or `null` for none
     */
    fun getDischargedCount(): Int? {
        return dischargedCount
    }

    /**
     * @param dischargedCount dischargedCount or `null` for none
     */
    fun setDischargedCount(dischargedCount: Int?): CommonResponse? {
        this.dischargedCount = dischargedCount
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDischargedList(): List<Patient?>? {
        return dischargedList
    }

    /**
     * @param dischargedList dischargedList or `null` for none
     */
    fun setDischargedList(dischargedList: List<Patient?>?): CommonResponse? {
        this.dischargedList = dischargedList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getErrorId(): Int? {
        return errorId
    }

    /**
     * @param errorId errorId or `null` for none
     */
    fun setErrorId(errorId: Int?): CommonResponse? {
        this.errorId = errorId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getErrorMessage(): String? {
        return errorMessage
    }

    /**
     * @param errorMessage errorMessage or `null` for none
     */
    fun setErrorMessage(errorMessage: String?): CommonResponse? {
        this.errorMessage = errorMessage
        return this
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    /**
     * @return value or `null` for none
     */
    fun getId(): Long? {
        return id
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): CommonResponse? {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPendingList(): List<Patient?>? {
        return pendingList
    }

    /**
     * @param pendingList pendingList or `null` for none
     */
    fun setPendingList(pendingList: List<Patient?>?): CommonResponse? {
        this.pendingList = pendingList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getUser(): User? {
        return user
    }

    /**
     * @param provider provider or `null` for none
     */
    fun setUser(provider: User?): CommonResponse? {
        user = provider
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getProviderList(): List<Provider?>? {
        return providerList
    }

    /**
     * @param providerList providerList or `null` for none
     */
    fun setProviderList(providerList: List<Provider?>?): CommonResponse? {
        this.providerList = providerList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRoom(): Room? {
        return room
    }

    /**
     * @param room room or `null` for none
     */
    fun setRoom(room: Room?): CommonResponse? {
        this.room = room
        return this
    }


    /**
     * @return value or `null` for none
     */
    fun getStatus(): Boolean? {
        return status
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: Boolean?): CommonResponse? {
        this.status = status
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getUnit(): Unit? {
        return unit
    }

    /**
     * @param unit unit or `null` for none
     */
    fun setUnit(unit: Unit?): CommonResponse? {
        this.unit = unit
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getWard(): Ward? {
        return ward
    }

    /**
     * @param ward ward or `null` for none
     */
    fun setWard(ward: Ward?): CommonResponse? {
        this.ward = ward
        return this
    }



}
