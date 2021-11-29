
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.appointmentEndpoints.model

import java.io.Serializable


class Role : Serializable {
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var accessType: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key @com.google.api.client.json.JsonString
    var id: Long? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key @com.google.api.client.json.JsonString
    var providerId: Long? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var roleType: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key @com.google.api.client.json.JsonString
    var time: Long? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var typeId: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var typeName: String? = null
        private set

    /**
     * @param accessType accessType or `null` for none
     */
    fun setAccessType(accessType: String?): Role {
        this.accessType = accessType
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): Role {
        this.id = id
        return this
    }

    /**
     * @param providerId providerId or `null` for none
     */
    fun setProviderId(providerId: Long?): Role {
        this.providerId = providerId
        return this
    }

    /**
     * @param roleType roleType or `null` for none
     */
    fun setRoleType(roleType: String?): Role {
        this.roleType = roleType
        return this
    }

    /**
     * @param time time or `null` for none
     */
    fun setTime(time: Long?): Role {
        this.time = time
        return this
    }

    /**
     * @param typeId typeId or `null` for none
     */
    fun setTypeId(typeId: String?): Role {
        this.typeId = typeId
        return this
    }

    /**
     * @param typeName typeName or `null` for none
     */
    fun setTypeName(typeName: String?): Role {
        this.typeName = typeName
        return this
    } //  @Override
    //  public Role set(String fieldName, Object value) {
    //    return (Role) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public Role clone() {
    //    return (Role) super.clone();
    //  }
}