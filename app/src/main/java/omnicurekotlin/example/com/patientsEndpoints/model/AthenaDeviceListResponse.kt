package omnicurekotlin.example.com.patientsEndpoints.model

class AthenaDeviceListResponse {

    var athenaDeviceDataList: List<AthenaDeviceData?>? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null
    var status: Boolean? = null

    @JvmName("getAthenaDeviceDataList1")
    fun getAthenaDeviceDataList(): List<AthenaDeviceData?>? {
        return athenaDeviceDataList
    }


    fun setAthenaDeviceDataList(athenaDeviceDataList: List<AthenaDeviceData?>?): AthenaDeviceListResponse {
        this.athenaDeviceDataList = athenaDeviceDataList
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): AthenaDeviceListResponse {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): AthenaDeviceListResponse {
        this.errorMessage = errorMessage
        return this
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): AthenaDeviceListResponse {
        this.id = id
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): AthenaDeviceListResponse {
        this.status = status
        return this
    }





}