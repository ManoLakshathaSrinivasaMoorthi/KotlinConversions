package omnicurekotlin.example.com.patientsEndpoints.model

class PatientListResponse {

     var count: Int? = null
     var errorId: Int? = null
     var errorMessage: String? = null
     var id: Long? = null
     var patientList: List<Patient?>? = null
     var status: Boolean? = null


    @JvmName("getCount1")
    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): PatientListResponse {
        this.count = count
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): PatientListResponse? {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): PatientListResponse {
        this.errorMessage = errorMessage
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): PatientListResponse {
        this.id = id
        return this
    }


    @JvmName("getPatientList1")
    fun getPatientList(): List<Patient?>? {
        return patientList
    }


    fun setPatientList(patientList: List<Patient?>?): PatientListResponse {
        this.patientList = patientList
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): PatientListResponse {
        this.status = status
        return this
    }


}
