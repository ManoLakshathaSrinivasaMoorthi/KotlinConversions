package omnicurekotlin.example.com.patientsEndpoints.model

class PatientHistoryResponse {

    private var count: Int? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var patientHistoryList: List<PatientHistory?>? = null
    private var status: Boolean? = null

    /**
     * @return value or `null` for none
     */
    fun getCount(): Int? {
        return count
    }

    /**
     * @param count count or `null` for none
     */
    fun setCount(count: Int?): PatientHistoryResponse? {
        this.count = count
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
    fun setErrorId(errorId: Int?): PatientHistoryResponse? {
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
    fun setErrorMessage(errorMessage: String?): PatientHistoryResponse? {
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
    fun setId(id: Long?): PatientHistoryResponse? {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPatientHistoryList(): List<PatientHistory?>? {
        return patientHistoryList
    }

    /**
     * @param patientHistoryList patientHistoryList or `null` for none
     */
    fun setPatientHistoryList(patientHistoryList: List<PatientHistory?>?): PatientHistoryResponse? {
        this.patientHistoryList = patientHistoryList
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
    fun setStatus(status: Boolean?): PatientHistoryResponse? {
        this.status = status
        return this
    }



}
