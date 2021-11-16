package omnicurekotlin.example.com.patientsEndpoints.model

class PatientResponse {

    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var patient: Patient? = null
    private var status: Boolean? = null


    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): PatientResponse? {
        this.errorId = errorId
        return this
    }


    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): PatientResponse? {
        this.errorMessage = errorMessage
        return this
    }


    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): PatientResponse? {
        this.id = id
        return this
    }


    fun getPatient(): Patient? {
        return patient
    }


    fun setPatient(patient: Patient?): PatientResponse? {
        this.patient = patient
        return this
    }


    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): PatientResponse? {
        this.status = status
        return this
    }





}