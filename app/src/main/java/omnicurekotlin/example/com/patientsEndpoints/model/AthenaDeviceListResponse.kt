package omnicurekotlin.example.com.patientsEndpoints.model

class AthenaDeviceListResponse {

    private var athenaDeviceDataList: List<AthenaDeviceData?>? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var status: Boolean? = null

    fun getAthenaDeviceDataList(): List<AthenaDeviceData?>? {
        return athenaDeviceDataList
    }

    /**
     * @param athenaDeviceDataList athenaDeviceDataList or `null` for none
     */
    fun setAthenaDeviceDataList(athenaDeviceDataList: List<AthenaDeviceData?>?): AthenaDeviceListResponse? {
        this.athenaDeviceDataList = athenaDeviceDataList
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
    fun setErrorId(errorId: Int?): AthenaDeviceListResponse? {
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
    fun setErrorMessage(errorMessage: String?): AthenaDeviceListResponse? {
        this.errorMessage = errorMessage
        return this
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
    fun setId(id: Long?): AthenaDeviceListResponse? {
        this.id = id
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
    fun setStatus(status: Boolean?): AthenaDeviceListResponse? {
        this.status = status
        return this
    }





}