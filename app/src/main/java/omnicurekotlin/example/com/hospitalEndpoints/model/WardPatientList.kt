package omnicurekotlin.example.com.hospitalEndpoints.model

class WardPatientList {

    private var wardName: String? = null
    private var count: Int? = null
    private var patientList: List<Patient?>? = null


    fun getWardName(): String? {
        return wardName
    }

    fun setWardName(wardName: String?): WardPatientList? {
        this.wardName = wardName
        return this
    }

    fun getCount(): Int? {
        return count
    }

    fun setCount(count: Int?): WardPatientList? {
        this.count = count
        return this
    }

    fun getPatientList(): List<Patient?>? {
        return patientList
    }

    fun setPatientList(patientList: List<Patient?>?): WardPatientList? {
        this.patientList = patientList
        return this
    }





}
