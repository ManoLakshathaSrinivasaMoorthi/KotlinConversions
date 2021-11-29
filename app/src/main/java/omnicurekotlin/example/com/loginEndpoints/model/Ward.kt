
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model

import java.io.Serializable


class Ward : Serializable {
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
    var occupiedBeds: Int? = null
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
    var virtual: Boolean? = null
        private set

    /**
     * @param hospital hospital or `null` for none
     */
    fun setHospital(hospital: String?): Ward {
        this.hospital = hospital
        return this
    }

    /**
     * @param hospitalId hospitalId or `null` for none
     */
    fun setHospitalId(hospitalId: Long?): Ward {
        this.hospitalId = hospitalId
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): Ward {
        this.id = id
        return this
    }

    /**
     * @param joiningTime joiningTime or `null` for none
     */
    fun setJoiningTime(joiningTime: Long?): Ward {
        this.joiningTime = joiningTime
        return this
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): Ward {
        this.name = name
        return this
    }

    /**
     * @param occupiedBeds occupiedBeds or `null` for none
     */
    fun setOccupiedBeds(occupiedBeds: Int?): Ward {
        this.occupiedBeds = occupiedBeds
        return this
    }

    /**
     * @param totalNumberOfBeds totalNumberOfBeds or `null` for none
     */
    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Ward {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    /**
     * @param totalNumberOfRooms totalNumberOfRooms or `null` for none
     */
    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Ward {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }

    /**
     * @param unitId unitId or `null` for none
     */
    fun setUnitId(unitId: Long?): Ward {
        this.unitId = unitId
        return this
    }

    /**
     * @param unitName unitName or `null` for none
     */
    fun setUnitName(unitName: String?): Ward {
        this.unitName = unitName
        return this
    }

    /**
     * @param virtual virtual or `null` for none
     */
    fun setVirtual(virtual: Boolean?): Ward {
        this.virtual = virtual
        return this
    } //  @Override
    //  public Ward set(String fieldName, Object value) {
    //    return (Ward) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public Ward clone() {
    //    return (Ward) super.clone();
    //  }
}