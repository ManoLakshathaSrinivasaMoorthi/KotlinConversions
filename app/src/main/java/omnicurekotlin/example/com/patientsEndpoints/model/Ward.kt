package omnicurekotlin.example.com.patientsEndpoints.model

class Ward {

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
     var virtual: Boolean? = null


    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }


    fun setHospital(hospital: String?):Ward {
        this.hospital = hospital
        return this
    }


    @JvmName("getHospitalId1")
    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?): Ward {
        this.hospitalId = hospitalId
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Ward {
        this.id = id
        return this
    }


    @JvmName("getJoiningTime1")
    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Ward {
        this.joiningTime = joiningTime
        return this
    }


    @JvmName("getName1")
    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Ward {
        this.name = name
        return this
    }


    @JvmName("getOccupiedBeds1")
    fun getOccupiedBeds(): Int? {
        return occupiedBeds
    }


    fun setOccupiedBeds(occupiedBeds: Int?): Ward {
        this.occupiedBeds = occupiedBeds
        return this
    }


    @JvmName("getTotalNumberOfBeds1")
    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }


    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?):Ward {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }


    @JvmName("getTotalNumberOfRooms1")
    fun getTotalNumberOfRooms(): Int? {
        return totalNumberOfRooms
    }


    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Ward {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }


    @JvmName("getUnitId1")
    fun getUnitId(): Long? {
        return unitId
    }


    fun setUnitId(unitId: Long?): Ward {
        this.unitId = unitId
        return this
    }


    @JvmName("getUnitName1")
    fun getUnitName(): String? {
        return unitName
    }


    fun setUnitName(unitName: String?): Ward {
        this.unitName = unitName
        return this
    }


    @JvmName("getVirtual1")
    fun getVirtual(): Boolean? {
        return virtual
    }

    fun setVirtual(virtual: Boolean?):Ward {
        this.virtual = virtual
        return this
    }



}
