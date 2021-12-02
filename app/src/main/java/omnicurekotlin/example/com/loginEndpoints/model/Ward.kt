
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class Ward : Serializable {

    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var joiningTime: Long? = null
    var name: String? = null
    private var occupiedBeds: Int? = null
    var totalNumberOfBeds: Int? = null
    var totalNumberOfRooms: Int? = null
    var unitId: Long? = null
    var unitName: String? = null
    private var virtual: Boolean? = null

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