package omnicurekotlin.example.com.providerEndpoints.model

class TermsConditions {

    private var id: String? = null
    private var name: String? = null
    private var value: String? = null


    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getValue(): String? {
        return value
    }

    fun setValue(value: String?) {
        this.value = value
    }
}
