package omnicurekotlin.example.com.providerEndpoints.model

class Unit {

    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var name: String? = null
    private var totalNumberOfBeds: Int? = null
    private var totalNumberOfRooms: Int? = null
    private var totalNumberOfWards: Int? = null


    fun getHospital(): String? {
        return hospital
    }


    fun setHospital(hospital: String?): Unit {
        this.hospital = hospital
        return this
    }


    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?): Unit {
        this.hospitalId = hospitalId
        return this
    }


    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Unit {
        this.id = id
        return this
    }


    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Unit {
        this.joiningTime = joiningTime
        return this
    }

    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Unit {
        this.name = name
        return this
    }


    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }

    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Unit {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    fun getTotalNumberOfRooms(): Int? {
        return totalNumberOfRooms
    }


    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Unit {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }


    fun getTotalNumberOfWards(): Int? {
        return totalNumberOfWards
    }


    fun setTotalNumberOfWards(totalNumberOfWards: Int?): Unit {
        this.totalNumberOfWards = totalNumberOfWards
        return this
    }



}
