package omnicurekotlin.example.com.patientsEndpoints.model

class PatientHistoryResponse {

     var count: Int? = null
     var errorId: Int? = null
     var errorMessage: String? = null
     var id: Long? = null
     var patientHistoryList: List<PatientHistory?>? = null
     var status: Boolean? = null


    @JvmName("getCount1")
    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): PatientHistoryResponse? {
        this.count = count
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): PatientHistoryResponse? {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): PatientHistoryResponse? {
        this.errorMessage = errorMessage
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): PatientHistoryResponse {
        this.id = id
        return this
    }


    @JvmName("getPatientHistoryList1")
    fun getPatientHistoryList(): List<PatientHistory?>? {
        return patientHistoryList
    }


    fun setPatientHistoryList(patientHistoryList: List<PatientHistory?>?): PatientHistoryResponse {
        this.patientHistoryList = patientHistoryList
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): PatientHistoryResponse {
        this.status = status
        return this
    }



}
