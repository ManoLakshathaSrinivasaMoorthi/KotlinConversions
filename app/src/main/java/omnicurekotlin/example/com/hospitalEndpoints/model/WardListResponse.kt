package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class WardListResponse : Serializable {

    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var status: Boolean? = null
    private var wardList: List<Ward?>? = null


    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?): WardListResponse {
        this.errorId = errorId
        return this
    }


    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): WardListResponse {
        this.errorMessage = errorMessage
        return this
    }

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): WardListResponse {
        this.id = id
        return this
    }

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): WardListResponse {
        this.status = status
        return this
    }

    fun getWardList(): List<Ward?>? {
        return wardList
    }

    fun setWardList(wardList: List<Ward?>?): WardListResponse {
        this.wardList = wardList
        return this
    }

//  @Override
//  public WardListResponse set(String fieldName, Object value) {
//    return (WardListResponse) super.set(fieldName, value);
//  }
//
//  @Override
//  public WardListResponse clone() {
//    return (WardListResponse) super.clone();
//  }

}
