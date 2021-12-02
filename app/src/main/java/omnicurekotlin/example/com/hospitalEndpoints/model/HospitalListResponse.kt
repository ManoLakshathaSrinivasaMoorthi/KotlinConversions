package omnicurekotlin.example.com.hospitalEndpoints.model

class HospitalListResponse {


    private var count: Int? = null
    private var errorId: Int? = null
    var errorMessage: String? = null
    var hospitalList: List<Hospital>?= null
    var id: Long? = null
    private var status: Boolean? = null

    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): HospitalListResponse {
        this.count = count
        return this
    }


    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): HospitalListResponse {
        this.errorId = errorId
        return this
    }




    fun setErrorMessage(errorMessage: String?): HospitalListResponse {
        this.errorMessage = errorMessage
        return this
    }




    fun setHospitalList(hospitalList: List<Hospital?>?): HospitalListResponse {
        this.hospitalList = hospitalList as List<Hospital>?
        return this
    }




    fun setId(id: Long?): HospitalListResponse? {
        this.id = id
        return this
    }

    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): HospitalListResponse {
        this.status = status
        return this
    }



}



