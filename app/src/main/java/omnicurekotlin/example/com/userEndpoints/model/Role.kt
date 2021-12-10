package omnicurekotlin.example.com.userEndpoints.model

class Role {

     var accessType: String? = null
     var id: Long? = null
     var providerId: Long? = null
     var roleType: String? = null
     var time: Long? = null
     var typeId: String? = null
     var typeName: String? = null


    @JvmName("getAccessType1")
    fun getAccessType(): String? {
        return accessType
    }

    fun setAccessType(accessType: String?): Role {
        this.accessType = accessType
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Role? {
        this.id = id
        return this
    }


    @JvmName("getProviderId1")
    fun getProviderId(): Long? {
        return providerId
    }

    fun setProviderId(providerId: Long?): Role? {
        this.providerId = providerId
        return this
    }


    @JvmName("getRoleType1")
    fun getRoleType(): String? {
        return roleType
    }


    fun setRoleType(roleType: String?): Role? {
        this.roleType = roleType
        return this
    }


    @JvmName("getTime1")
    fun getTime(): Long? {
        return time
    }


    fun setTime(time: Long?): Role? {
        this.time = time
        return this
    }


    @JvmName("getTypeId1")
    fun getTypeId(): String? {
        return typeId
    }


    fun setTypeId(typeId: String?): Role? {
        this.typeId = typeId
        return this
    }

    @JvmName("getTypeName1")
    fun getTypeName(): String? {
        return typeName
    }


    fun setTypeName(typeName: String?): Role? {
        this.typeName = typeName
        return this
    }


}
