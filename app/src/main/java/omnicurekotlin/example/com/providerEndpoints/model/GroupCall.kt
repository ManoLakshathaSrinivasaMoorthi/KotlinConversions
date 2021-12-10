package omnicurekotlin.example.com.providerEndpoints.model

class GroupCall {

     lateinit var receiverIds: Array<String?>
     var id: String? = null
     var token: String? = null
     var type: String? = null
     var patientsId: String? = null
     var message: String? = null


    @JvmName("getReceiverIds1")
    fun getReceiverIds(): Array<String?> {
        return receiverIds
    }

    @JvmName("setReceiverIds1")
    fun setReceiverIds(receiverIds: Array<String?>) {
        this.receiverIds = receiverIds
    }

    @JvmName("getId1")
    fun getId(): String? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: String?) {
        this.id = id
    }

    @JvmName("getToken1")
    fun getToken(): String? {
        return token
    }

    @JvmName("setToken1")
    fun setToken(token: String?) {
        this.token = token
    }

    @JvmName("getType1")
    fun getType(): String? {
        return type
    }

    @JvmName("setType1")
    fun setType(type: String?) {
        this.type = type
    }

    @JvmName("getPatientsId1")
    fun getPatientsId(): String? {
        return patientsId
    }

    @JvmName("setPatientsId1")
    fun setPatientsId(patientsId: String?) {
        this.patientsId = patientsId
    }

    @JvmName("getMessage1")
    fun getMessage(): String? {
        return message
    }

    @JvmName("setMessage1")
    fun setMessage(message: String?) {
        this.message = message
    }



}