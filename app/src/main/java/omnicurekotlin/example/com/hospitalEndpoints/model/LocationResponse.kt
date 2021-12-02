package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class LocationResponse :Serializable {

    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var itemList: List<Hospital?>? = null
    private var provider: Provider? = null
    private var status: Boolean? = null

    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): LocationResponse {
        this.errorId = errorId
        return this
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?): LocationResponse {
        this.errorMessage = errorMessage
        return this
    }


    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): LocationResponse {
        this.id = id
        return this
    }

    fun getItemList(): List<Hospital?>? {
        return itemList
    }

    fun setItemList(itemList: List<Hospital?>?): LocationResponse {
        this.itemList = itemList
        return this
    }

    fun getProvider(): Provider? {
        return provider
    }

    fun setProvider(provider: Provider?): LocationResponse {
        this.provider = provider
        return this
    }


    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): LocationResponse {
        this.status = status
        return this
    }

//  @Override
//  public LocationResponse set(String fieldName, Object value) {
//    return (LocationResponse) super.set(fieldName, value);
//  }
//
//  @Override
//  public LocationResponse clone() {
//    return (LocationResponse) super.clone();
//  }

}
