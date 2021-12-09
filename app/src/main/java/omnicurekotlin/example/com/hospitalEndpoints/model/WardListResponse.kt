package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class WardListResponse : Serializable {

    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null
    var status: Boolean? = null
    var wardList: List<Ward?>? = null


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?): WardListResponse {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): WardListResponse {
        this.errorMessage = errorMessage
        return this
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): WardListResponse {
        this.id = id
        return this
    }

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): WardListResponse {
        this.status = status
        return this
    }

    @JvmName("getWardList1")
    fun getWardList(): List<Ward?>? {
        return wardList
    }

    fun setWardList(wardList: List<Ward?>?): WardListResponse {
        this.wardList = wardList
        return this
    }


}
