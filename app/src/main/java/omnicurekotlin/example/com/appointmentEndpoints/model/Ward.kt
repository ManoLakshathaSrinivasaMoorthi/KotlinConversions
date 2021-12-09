
package omnicurekotlin.example.com.appointmentEndpoints.model

import java.io.Serializable


class Ward : Serializable {

    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var joiningTime: Long? = null
    var name: String? = null
    var occupiedBeds: Int? = null
    var totalNumberOfBeds: Int? = null
    var totalNumberOfRooms: Int? = null
    var unitId: Long? = null
    var unitName: String? = null
    private var virtual: Boolean? = null


    fun setHospital(hospital: String?): Ward {
        this.hospital = hospital
        return this
    }


    fun setHospitalId(hospitalId: Long?): Ward {
        this.hospitalId = hospitalId
        return this
    }


    fun setId(id: Long?): Ward {
        this.id = id
        return this
    }


    fun setJoiningTime(joiningTime: Long?): Ward {
        this.joiningTime = joiningTime
        return this
    }


    fun setName(name: String?): Ward {
        this.name = name
        return this
    }


    fun setOccupiedBeds(occupiedBeds: Int?): Ward {
        this.occupiedBeds = occupiedBeds
        return this
    }


    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Ward {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }


    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Ward {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }


    fun setUnitId(unitId: Long?): Ward {
        this.unitId = unitId
        return this
    }


    fun setUnitName(unitName: String?): Ward {
        this.unitName = unitName
        return this
    }


    fun setVirtual(virtual: Boolean?): Ward {
        this.virtual = virtual
        return this
    }
}