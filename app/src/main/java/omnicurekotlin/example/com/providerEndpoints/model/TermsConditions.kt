package omnicurekotlin.example.com.providerEndpoints.model

class TermsConditions {

     var id: String? = null
     var name: String? = null
     var value: String? = null


    @JvmName("getId1")
    fun getId(): String? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: String?) {
        this.id = id
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }

    @JvmName("getValue1")
    fun getValue(): String? {
        return value
    }

    @JvmName("setValue1")
    fun setValue(value: String?) {
        this.value = value
    }
}
