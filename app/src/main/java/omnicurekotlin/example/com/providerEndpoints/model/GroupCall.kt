package omnicurekotlin.example.com.providerEndpoints.model

class GroupCall {

    private lateinit var receiverIds: Array<String?>
    private var id: String? = null
    private var token: String? = null
    private var type: String? = null
    private var patientsId: String? = null
    private var message: String? = null


    fun getReceiverIds(): Array<String?>? {
        return receiverIds
    }

    fun setReceiverIds(receiverIds: Array<String?>) {
        this.receiverIds = receiverIds
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getPatientsId(): String? {
        return patientsId
    }

    fun setPatientsId(patientsId: String?) {
        this.patientsId = patientsId
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }



}