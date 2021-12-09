package omnicurekotlin.example.com.userEndpoints.model

class Room {


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


    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }


    fun setHospital(hospital: String?): Room {
        this.hospital = hospital
        return this
    }


    @JvmName("getHospitalId1")
    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?): Room {
        this.hospitalId = hospitalId
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Room {
        this.id = id
        return this
    }


    @JvmName("getJoiningTime1")
    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Room {
        this.joiningTime = joiningTime
        return this
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Room {
        this.name = name
        return this
    }


    @JvmName("getTotalNumberOfBeds1")
    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }

    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Room {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    @JvmName("getUnitId1")
    fun getUnitId(): Long? {
        return unitId
    }


    fun setUnitId(unitId: Long?): Room {
        this.unitId = unitId
        return this
    }


    @JvmName("getUnitName1")
    fun getUnitName(): String? {
        return unitName
    }


    fun setUnitName(unitName: String?): Room {
        this.unitName = unitName
        return this
    }

    @JvmName("getWard1")
    fun getWard(): String? {
        return ward
    }


    fun setWard(ward: String?): Room {
        this.ward = ward
        return this
    }


    @JvmName("getWardId1")
    fun getWardId(): Long? {
        return wardId
    }

    fun setWardId(wardId: Long?): Room {
        this.wardId = wardId
        return this
    }






}