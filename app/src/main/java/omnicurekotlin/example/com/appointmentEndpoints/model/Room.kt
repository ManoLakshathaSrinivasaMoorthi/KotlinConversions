
package omnicurekotlin.example.com.appointmentEndpoints.model

import java.io.Serializable



class Room : Serializable {


    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var joiningTime: Long? = null
    var name: String? = null
    var totalNumberOfBeds: Int? = null
    var unitId: Long? = null
    var unitName: String? = null
    var ward: String? = null
    var wardId: Long? = null


    fun setHospital(hospital: String?): Room {
        this.hospital = hospital
        return this
    }


    fun setHospitalId(hospitalId: Long?): Room {
        this.hospitalId = hospitalId
        return this
    }


    fun setId(id: Long?): Room {
        this.id = id
        return this
    }


    fun setJoiningTime(joiningTime: Long?): Room {
        this.joiningTime = joiningTime
        return this
    }


    fun setName(name: String?): Room {
        this.name = name
        return this
    }


    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Room {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }


    fun setUnitId(unitId: Long?): Room {
        this.unitId = unitId
        return this
    }


    fun setUnitName(unitName: String?): Room {
        this.unitName = unitName
        return this
    }


    fun setWard(ward: String?): Room {
        this.ward = ward
        return this
    }

    fun setWardId(wardId: Long?): Room {
        this.wardId = wardId
        return this
    }
}