
package omnicurekotlin.example.com.appointmentEndpoints.model

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
        private set


    fun setHospital(hospital: String?): Unit {
        this.hospital = hospital
        return this
    }


    fun setHospitalId(hospitalId: Long?): Unit {
        this.hospitalId = hospitalId
        return this
    }

    fun setId(id: Long?): Unit {
        this.id = id
        return this
    }


    fun setJoiningTime(joiningTime: Long?): Unit {
        this.joiningTime = joiningTime
        return this
    }

    fun setName(name: String?): Unit {
        this.name = name
        return this
    }


    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Unit {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }


    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Unit {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }


    fun setTotalNumberOfWards(totalNumberOfWards: Int?): Unit {
        this.totalNumberOfWards = totalNumberOfWards
        return this
    }
}