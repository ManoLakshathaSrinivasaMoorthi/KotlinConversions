package omnicurekotlin.example.com.providerEndpoints.model

class TeamMemberResponse {

    private var name: String? = null
    private var members: List<Members?>? = null

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getMembers(): List<Members?>? {
        return members
    }

    fun setMembers(members: List<Members?>?) {
        this.members = members
    }
}
