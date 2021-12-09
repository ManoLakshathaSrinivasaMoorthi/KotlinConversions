package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class BedListResponse:Serializable {

    var bedList: List<Bed?>? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null
    var status: Boolean? = null


    @JvmName("getBedList1")
    fun getBedList(): List<Bed?>? {
        return bedList
    }

    fun setBedList(bedList: List<Bed?>?): BedListResponse {
        this.bedList = bedList
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): BedListResponse {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): BedListResponse {
        this.errorMessage = errorMessage
        return this
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): BedListResponse {
        this.id = id
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): BedListResponse {
        this.status = status
        return this
    }


}
