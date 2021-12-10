package omnicurekotlin.example.com.patientsEndpoints.model

class TransferHospitalListResponse {

     var status: Boolean? = null
     var errorId: Int? = null
     var hospitalList: List<HospitalList?>? = null
     var currentPatient: CurrentPatient? = null



    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean?) {
        this.status = status
    }

    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }

    @JvmName("setErrorId1")
    fun setErrorId(errorId: Int?) {
        this.errorId = errorId
    }

    @JvmName("getHospitalList1")
    fun getHospitalList(): List<HospitalList?>? {
        return hospitalList
    }

    @JvmName("setHospitalList1")
    fun setHospitalList(hospitalList: List<HospitalList?>?) {
        this.hospitalList = hospitalList
    }

    @JvmName("getCurrentPatient1")
    fun getCurrentPatient(): CurrentPatient? {
        return currentPatient
    }

    @JvmName("setCurrentPatient1")
    fun setCurrentPatient(currentPatient: CurrentPatient?) {
        this.currentPatient = currentPatient
    }



}
