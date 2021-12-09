package omnicurekotlin.example.com.userEndpoints.model

class Unit {

     var hospital: String? = null
     var hospitalId: Long? = null
     var id: Long? = null
     var joiningTime: Long? = null
     var name: String? = null
     var totalNumberOfBeds: Int? = null
     var totalNumberOfRooms: Int? = null
     var totalNumberOfWards: Int? = null


    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): Unit {
        this.hospital = hospital
        return this
    }

    @JvmName("getHospitalId1")
    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?): Unit {
        this.hospitalId = hospitalId
        return this
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Unit {
        this.id = id
        return this
    }

    @JvmName("getJoiningTime1")
    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: Long?): Unit {
        this.joiningTime = joiningTime
        return this
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Unit {
        this.name = name
        return this
    }

    @JvmName("getTotalNumberOfBeds1")
    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }

    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Unit {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    @JvmName("getTotalNumberOfRooms1")
    fun getTotalNumberOfRooms(): Int? {
        return totalNumberOfRooms
    }

    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Unit? {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }

    @JvmName("getTotalNumberOfWards1")
    fun getTotalNumberOfWards(): Int? {
        return totalNumberOfWards
    }


    fun setTotalNumberOfWards(totalNumberOfWards: Int?): Unit? {
        this.totalNumberOfWards = totalNumberOfWards
        return this
    }



}
