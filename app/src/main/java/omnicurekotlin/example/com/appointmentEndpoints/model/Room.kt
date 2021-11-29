
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.appointmentEndpoints.model

import java.io.Serializable



class Room : Serializable {
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */

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
    //com.google.api.client.util.Key @com.google.api.client.json.JsonString
    var unitId: Long? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var unitName: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var ward: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key @com.google.api.client.json.JsonString
    var wardId: Long? = null
        private set

    /**
     * @param hospital hospital or `null` for none
     */
    fun setHospital(hospital: String?): Room {
        this.hospital = hospital
        return this
    }

    /**
     * @param hospitalId hospitalId or `null` for none
     */
    fun setHospitalId(hospitalId: Long?): Room {
        this.hospitalId = hospitalId
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): Room {
        this.id = id
        return this
    }

    /**
     * @param joiningTime joiningTime or `null` for none
     */
    fun setJoiningTime(joiningTime: Long?): Room {
        this.joiningTime = joiningTime
        return this
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): Room {
        this.name = name
        return this
    }

    /**
     * @param totalNumberOfBeds totalNumberOfBeds or `null` for none
     */
    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Room {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    /**
     * @param unitId unitId or `null` for none
     */
    fun setUnitId(unitId: Long?): Room {
        this.unitId = unitId
        return this
    }

    /**
     * @param unitName unitName or `null` for none
     */
    fun setUnitName(unitName: String?): Room {
        this.unitName = unitName
        return this
    }

    /**
     * @param ward ward or `null` for none
     */
    fun setWard(ward: String?): Room {
        this.ward = ward
        return this
    }

    /**
     * @param wardId wardId or `null` for none
     */
    fun setWardId(wardId: Long?): Room {
        this.wardId = wardId
        return this
    } //  @Override
    //  public Room set(String fieldName, Object value) {
    //    return (Room) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public Room clone() {
    //    return (Room) super.clone();
    //  }
}