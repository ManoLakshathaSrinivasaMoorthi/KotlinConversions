package omnicurekotlin.example.com.patientsEndpoints.model

class DocBoxPatientListResponse {

     var athenaDeviceDataList: List<AthenaDeviceData?>? = null
     var docBoxPatientList: List<DocBoxPatient?>? = null
     var errorId: Int? = null
     var errorMessage: String? = null
     var id: Long? = null
     var status: Boolean? = null


    @JvmName("getAthenaDeviceDataList1")
    fun getAthenaDeviceDataList(): List<AthenaDeviceData?>? {
        return athenaDeviceDataList
    }


    fun setAthenaDeviceDataList(athenaDeviceDataList: List<AthenaDeviceData?>?): DocBoxPatientListResponse {
        this.athenaDeviceDataList = athenaDeviceDataList
        return this
    }

    @JvmName("getDocBoxPatientList1")
    fun getDocBoxPatientList(): List<DocBoxPatient?>? {
        return docBoxPatientList
    }


    fun setDocBoxPatientList(docBoxPatientList: List<DocBoxPatient?>?): DocBoxPatientListResponse {
        this.docBoxPatientList = docBoxPatientList
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): DocBoxPatientListResponse {
        this.errorId = errorId
        return this
    }

    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): DocBoxPatientListResponse {
        this.errorMessage = errorMessage
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): DocBoxPatientListResponse {
        this.id = id
        return this
    }

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): DocBoxPatientListResponse {
        this.status = status
        return this
    }
}
