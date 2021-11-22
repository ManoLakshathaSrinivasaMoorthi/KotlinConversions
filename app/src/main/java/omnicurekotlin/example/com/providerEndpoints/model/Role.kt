package omnicurekotlin.example.com.providerEndpoints.model

class Role {

    private var accessType: String? = null
    private var id: Long? = null
    private var providerId: Long? = null
    private var roleType: String? = null
    private var time: Long? = null
    private var typeId: String? = null
    private var typeName: String? = null


    fun getAccessType(): String? {
        return accessType
    }


    fun setAccessType(accessType: String?): Role? {
        this.accessType = accessType
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?):Role? {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getProviderId(): Long? {
        return providerId
    }

    /**
     * @param providerId providerId or `null` for none
     */
    fun setProviderId(providerId: Long?): Role? {
        this.providerId = providerId
        return this
    }


    fun getRoleType(): String? {
        return roleType
    }

    /**
     * @param roleType roleType or `null` for none
     */
    fun setRoleType(roleType: String?): Role? {
        this.roleType = roleType
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getTime(): Long? {
        return time
    }

    /**
     * @param time time or `null` for none
     */
    fun setTime(time: Long?): Role? {
        this.time = time
        return this
    }


    fun getTypeId(): String? {
        return typeId
    }


    fun setTypeId(typeId: String?):Role? {
        this.typeId = typeId
        return this
    }


    fun getTypeName(): String? {
        return typeName
    }


    fun setTypeName(typeName: String?): Role? {
        this.typeName = typeName
        return this
    }


}
