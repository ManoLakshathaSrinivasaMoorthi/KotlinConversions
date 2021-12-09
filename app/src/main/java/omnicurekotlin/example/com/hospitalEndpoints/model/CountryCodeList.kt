package omnicurekotlin.example.com.hospitalEndpoints.model

class CountryCodeList {

     var id: String? = null
     var name: String? = null
     var code: String? = null


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

    @JvmName("getCode1")
    fun getCode(): String? {
        return code
    }

    @JvmName("setCode1")
    fun setCode(code: String?) {
        this.code = code
    }
}
