package omnicurekotlin.example.com.patientsEndpoints.model

class DocBoxPatientListResponse {

    private var athenaDeviceDataList: List<AthenaDeviceData?>? = null

    private var docBoxPatientList: List<DocBoxPatient?>? = null

    private var errorId: Int? = null
    private var errorMessage: String? = null

    private var id: Long? = null
    private var status: Boolean? = null


    fun getAthenaDeviceDataList(): List<AthenaDeviceData?>? {
        return athenaDeviceDataList
    }


    fun setAthenaDeviceDataList(athenaDeviceDataList: List<AthenaDeviceData?>?): DocBoxPatientListResponse? {
        this.athenaDeviceDataList = athenaDeviceDataList
        return this
    }

    fun getDocBoxPatientList(): List<DocBoxPatient?>? {
        return docBoxPatientList
    }


    fun setDocBoxPatientList(docBoxPatientList: List<DocBoxPatient?>?): DocBoxPatientListResponse? {
        this.docBoxPatientList = docBoxPatientList
        return this
    }


    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): DocBoxPatientListResponse? {
        this.errorId = errorId
        return this
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): DocBoxPatientListResponse? {
        this.errorMessage = errorMessage
        return this
    }


    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): DocBoxPatientListResponse? {
        this.id = id
        return this
    }

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): DocBoxPatientListResponse? {
        this.status = status
        return this
    }



}
