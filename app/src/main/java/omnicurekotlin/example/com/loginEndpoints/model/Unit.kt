
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class Unit : Serializable {

    var hospital: String? = null

    var hospitalId: Long? = null

    var id: Long? = null

    var joiningTime: Long? = null

    var name: String? = null

    var totalNumberOfBeds: Int? = null

    var totalNumberOfRooms: Int? = null

    private var totalNumberOfWards: Int? = null

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
    } //  @Override
    //  public Unit set(String fieldName, Object value) {
    //    return (Unit) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public Unit clone() {
    //    return (Unit) super.clone();
    //  }
}