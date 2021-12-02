package omnicurekotlin.example.com.userEndpoints.model


class CountryCodeList {

    private var id = 0.0
    private var name: String? = null
    private var code: String? = null


    fun getId(): String? {
        return id.toString()
    }

    fun setId(id: Double) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getCode(): String? {
        return code
    }

    fun setCode(code: String?) {
        this.code = code
    }
}
