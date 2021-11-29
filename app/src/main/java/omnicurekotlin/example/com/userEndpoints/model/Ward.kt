package omnicurekotlin.example.com.userEndpoints.model

class Ward {

    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var name: String? = null
    private var occupiedBeds: Int? = null
    private var totalNumberOfBeds: Int? = null
    private var totalNumberOfRooms: Int? = null
    private var unitId: Long? = null
    private var unitName: String? = null
    private var virtual: Boolean? = null


    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): Ward? {
        this.hospital = hospital
        return this
    }

    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?): Ward? {
        this.hospitalId = hospitalId
        return this
    }


    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): Ward? {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: Long?): Ward? {
        this.joiningTime = joiningTime
        return this
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Ward? {
        this.name = name
        return this
    }

    fun getOccupiedBeds(): Int? {
        return occupiedBeds
    }

    fun setOccupiedBeds(occupiedBeds: Int?): Ward? {
        this.occupiedBeds = occupiedBeds
        return this
    }

    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }

    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Ward? {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    fun getTotalNumberOfRooms(): Int? {
        return totalNumberOfRooms
    }

    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?): Ward? {
        this.totalNumberOfRooms = totalNumberOfRooms
        return this
    }

    fun getUnitId(): Long? {
        return unitId
    }

    fun setUnitId(unitId: Long?): Ward? {
        this.unitId = unitId
        return this
    }

    fun getUnitName(): String? {
        return unitName
    }

    fun setUnitName(unitName: String?): Ward? {
        this.unitName = unitName
        return this
    }

    fun getVirtual(): Boolean? {
        return virtual
    }


    fun setVirtual(virtual: Boolean?): Ward? {
        this.virtual = virtual
        return this
    }

   /* operator fun set(fieldName: String?, value: Any?): Ward? {
        return super.set(fieldName, value) as Ward?
    }

    fun clone(): Ward? {
        return super.clone() as Ward?
    }*/

}





