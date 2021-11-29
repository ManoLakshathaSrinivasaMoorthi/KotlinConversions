
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.appointmentEndpoints.model

import java.io.Serializable

class Unit : Serializable {
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var hospital: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key @com.google.api.client.json.JsonString
    var hospitalId: Long? = null
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
    var joiningTime: Long? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var name: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var totalNumberOfBeds: Int? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var totalNumberOfRooms: Int? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var totalNumberOfWards: Int? = null
        private set

    /**
     * @param hospital hospital or `null` for none
     */
    fun setHospital(hospital: String?): Unit {
        this.hospital = hospital
        return this
    }

    /**
     * @param hospitalId hospitalId or `null` for none
     */
    fun setHospitalId(hospitalId: Long?): Unit {
        this.hospitalId = hospitalId
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): Unit {
        this.id = id
        return this
    }

    /**
     * @param joiningTime joiningTime or `null` for none
     */
    fun setJoiningTime(joiningTime: Long?): Unit {
        this.joiningTime = joiningTime
        return this
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): Unit {
        this.name = name
        return this
    }

    /**
     * @param totalNumberOfBeds totalNumberOfBeds or `null` for none
     */
    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Unit {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    /**
     * @param totalNumberOfRooms totalNumberOfRooms or `null` for none
     */
    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Unit {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }

    /**
     * @param totalNumberOfWards totalNumberOfWards or `null` for none
     */
    fun setTotalNumberOfWards(totalNumberOfWards: Int?): Unit {
        this.totalNumberOfWards = totalNumberOfWards
        return this
    }
}