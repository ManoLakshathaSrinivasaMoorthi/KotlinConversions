package omnicurekotlin.example.com.providerEndpoints.model

class Teams {

     var name: String? = null
     var members: List<TeamMembers?>? = null


    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }

    @JvmName("getMembers1")
    fun getMembers(): List<TeamMembers?>? {
        return members
    }

    @JvmName("setMembers1")
    fun setMembers(members: List<TeamMembers?>?) {
        this.members = members
    }

}
