package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class BedListResponse:Serializable {

    private var bedList: List<Bed?>? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var status: Boolean? = null


    fun getBedList(): List<Bed?>? {
        return bedList
    }

    /**
     * @param bedList bedList or `null` for none
     */
    fun setBedList(bedList: List<Bed?>?): BedListResponse {
        this.bedList = bedList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getErrorId(): Int? {
        return errorId
    }

    /**
     * @param errorId errorId or `null` for none
     */
    fun setErrorId(errorId: Int?): BedListResponse {
        this.errorId = errorId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getErrorMessage(): String? {
        return errorMessage
    }

    /**
     * @param errorMessage errorMessage or `null` for none
     */
    fun setErrorMessage(errorMessage: String?): BedListResponse {
        this.errorMessage = errorMessage
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getId(): Long? {
        return id
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): BedListResponse {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getStatus(): Boolean? {
        return status
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: Boolean?): BedListResponse {
        this.status = status
        return this
    }

//  @Override
//  public BedListResponse set(String fieldName, Object value) {
//    return (BedListResponse) super.set(fieldName, value);
//  }
//
//  @Override
//  public BedListResponse clone() {
//    return (BedListResponse) super.clone();
//  }

}
