package omnicurekotlin.example.com.hospitalEndpoints.model

class WardPatientList {

     var wardName: String? = null
     var count: Int? = null
     var patientList: List<Patient?>? = null


    @JvmName("getWardName1")
    fun getWardName(): String? {
        return wardName
    }

    fun setWardName(wardName: String?): WardPatientList {
        this.wardName = wardName
        return this
    }

    @JvmName("getCount1")
    fun getCount(): Int? {
        return count
    }

    fun setCount(count: Int?): WardPatientList {
        this.count = count
        return this
    }

    @JvmName("getPatientList1")
    fun getPatientList(): List<Patient?>? {
        return patientList
    }

    fun setPatientList(patientList: List<Patient?>?): WardPatientList {
        this.patientList = patientList
        return this
    }





}
