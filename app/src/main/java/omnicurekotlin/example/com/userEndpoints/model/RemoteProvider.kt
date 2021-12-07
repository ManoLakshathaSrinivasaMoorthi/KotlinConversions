package omnicurekotlin.example.com.userEndpoints.model

class RemoteProvider {

     var id: String? = null
     var type: String? = null
     var name: String? = null
     var acceptConsultFlag: Boolean? = null
     var status: Boolean? = null
     var orderId: Long? = null

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }

    @JvmName("getId1")
    fun getId(): String? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: String?) {
        this.id = id
    }

    @JvmName("getType1")
    fun getType(): String? {
        return type
    }

    @JvmName("setType1")
    fun setType(type: String?) {
        this.type = type
    }

    @JvmName("getAcceptConsultFlag1")
    fun getAcceptConsultFlag(): Boolean? {
        return acceptConsultFlag
    }

    @JvmName("setAcceptConsultFlag1")
    fun setAcceptConsultFlag(acceptConsultFlag: Boolean?) {
        this.acceptConsultFlag = acceptConsultFlag
    }

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean?) {
        this.status = status
    }

    @JvmName("getOrderId1")
    fun getOrderId(): Long? {
        return orderId
    }

    @JvmName("setOrderId1")
    fun setOrderId(orderId: Long?) {
        this.orderId = orderId
    }
}
