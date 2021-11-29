package omnicurekotlin.example.com.patientsEndpoints.model

class TransferHospitalListResponse {

    private var status: Boolean? = null
    private var errorId: Int? = null
    private var hospitalList: List<HospitalList?>? = null


    private var currentPatient: CurrentPatient? = null



    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?) {
        this.errorId = errorId
    }

    fun getHospitalList(): List<HospitalList?>? {
        return hospitalList
    }

    fun setHospitalList(hospitalList: List<HospitalList?>?) {
        this.hospitalList = hospitalList
    }

    fun getCurrentPatient(): CurrentPatient? {
        return currentPatient
    }

    fun setCurrentPatient(currentPatient: CurrentPatient?) {
        this.currentPatient = currentPatient
    }



}
