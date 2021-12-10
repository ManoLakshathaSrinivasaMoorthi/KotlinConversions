package omnicurekotlin.example.com.userEndpoints.model


class CountryCodeList {

    var id = 0.0
    var name: String? = null
    var code: String? = null


    fun getId(): String {
        return id.toString()
    }

    @JvmName("setId1")
    fun setId(id: Double) {
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

    @JvmName("getCode1")
    fun getCode(): String? {
        return code
    }

    @JvmName("setCode1")
    fun setCode(code: String?) {
        this.code = code
    }
}
