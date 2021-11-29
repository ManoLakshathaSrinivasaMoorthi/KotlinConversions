package omnicurekotlin.example.com.patientsEndpoints.model

class PatientListResponse {

    private var count: Int? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var patientList: List<Patient?>? = null
    private var status: Boolean? = null


    fun getCount(): Int? {
        return count
    }

    /**
     * @param count count or `null` for none
     */
    fun setCount(count: Int?): PatientListResponse? {
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
    fun setErrorId(errorId: Int?): PatientListResponse? {
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
    fun setErrorMessage(errorMessage: String?): PatientListResponse? {
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
    fun setId(id: Long?): PatientListResponse? {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPatientList(): List<Patient?>? {
        return patientList
    }

    /**
     * @param patientList patientList or `null` for none
     */
    fun setPatientList(patientList: List<Patient?>?): PatientListResponse? {
        this.patientList = patientList
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
    fun setStatus(status: Boolean?): PatientListResponse? {
        this.status = status
        return this
    }


}
