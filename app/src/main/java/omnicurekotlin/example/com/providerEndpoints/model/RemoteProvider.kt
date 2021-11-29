package omnicurekotlin.example.com.providerEndpoints.model

class RemoteProvider {

    private var id: String? = null
    private var type: String? = null
    private var name: String? = null
    private var acceptConsultFlag: Boolean? = null
    private var status: Boolean? = null

    private var orderId: Long? = null

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getAcceptConsultFlag(): Boolean? {
        return acceptConsultFlag
    }

    fun setAcceptConsultFlag(acceptConsultFlag: Boolean?) {
        this.acceptConsultFlag = acceptConsultFlag
    }

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getOrderId(): Long? {
        return orderId
    }

    fun setOrderId(orderId: Long?) {
        this.orderId = orderId
    }
}
