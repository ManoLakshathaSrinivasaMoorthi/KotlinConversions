package omnicurekotlin.example.com.providerEndpoints.model

class Teams {

    private var name: String? = null
    private var members: List<TeamMembers?>? = null


    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getMembers(): List<TeamMembers?>? {
        return members
    }

    fun setMembers(members: List<TeamMembers?>?) {
        this.members = members
    }

}
