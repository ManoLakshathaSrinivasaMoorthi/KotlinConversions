package omnicurekotlin.example.com.patientsEndpoints.model

class PatientResponse {

     var errorId: Int? = null
     var errorMessage: String? = null
     var id: Long? = null
     var patient: Patient? = null
     var status: Boolean? = null


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): PatientResponse {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): PatientResponse {
        this.errorMessage = errorMessage
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): PatientResponse {
        this.id = id
        return this
    }


    @JvmName("getPatient1")
    fun getPatient(): Patient? {
        return patient
    }


    fun setPatient(patient: Patient?): PatientResponse {
        this.patient = patient
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): PatientResponse {
        this.status = status
        return this
    }





}