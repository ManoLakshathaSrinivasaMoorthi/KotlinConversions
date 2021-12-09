package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class LocationResponse :Serializable {

    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null
    var itemList: List<Hospital?>? = null
    var provider: Provider? = null
    var status: Boolean? = null

    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): LocationResponse {
        this.errorId = errorId
        return this
    }

    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?): LocationResponse {
        this.errorMessage = errorMessage
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): LocationResponse {
        this.id = id
        return this
    }

    @JvmName("getItemList1")
    fun getItemList(): List<Hospital?>? {
        return itemList
    }

    fun setItemList(itemList: List<Hospital?>?): LocationResponse {
        this.itemList = itemList
        return this
    }

    @JvmName("getProvider1")
    fun getProvider(): Provider? {
        return provider
    }

    fun setProvider(provider: Provider?): LocationResponse {
        this.provider = provider
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): LocationResponse {
        this.status = status
        return this
    }


}
