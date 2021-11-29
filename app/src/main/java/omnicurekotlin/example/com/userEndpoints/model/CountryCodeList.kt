package omnicurekotlin.example.com.userEndpoints.model


class CountryCodeList {


    var id: String? = null
    private var name: String? = null
    var code: String? = null


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

    fun getCode(): String? {
        return code
    }

    fun setCode(code: String?) {
        this.code = code
    }
}