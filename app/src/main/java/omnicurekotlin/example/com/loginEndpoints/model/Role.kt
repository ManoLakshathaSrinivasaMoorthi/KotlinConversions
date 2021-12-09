
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class Role : Serializable {

    var accessType: String? = null
    var id: Long? = null
    var providerId: Long? = null
    var roleType: String? = null
    var time: Long? = null
    var typeId: String? = null
    var typeName: String? = null

    fun setAccessType(accessType: String?): Role {
        this.accessType = accessType
        return this
    }


    fun setId(id: Long?): Role {
        this.id = id
        return this
    }

    fun setProviderId(providerId: Long?): Role {
        this.providerId = providerId
        return this
    }

    fun setRoleType(roleType: String?): Role {
        this.roleType = roleType
        return this
    }

    fun setTime(time: Long?): Role {
        this.time = time
        return this
    }


    fun setTypeId(typeId: String?): Role {
        this.typeId = typeId
        return this
    }


    fun setTypeName(typeName: String?): Role {
        this.typeName = typeName
        return this
    }
}