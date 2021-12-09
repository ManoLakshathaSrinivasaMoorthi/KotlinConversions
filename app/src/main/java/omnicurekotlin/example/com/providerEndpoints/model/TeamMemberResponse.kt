package omnicurekotlin.example.com.providerEndpoints.model

class TeamMemberResponse {

     var name: String? = null
     var members: List<Members?>? = null

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }

    @JvmName("getMembers1")
    fun getMembers(): List<Members?>? {
        return members
    }

    @JvmName("setMembers1")
    fun setMembers(members: List<Members?>?) {
        this.members = members
    }
}
