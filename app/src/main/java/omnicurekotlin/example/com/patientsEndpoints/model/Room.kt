package omnicurekotlin.example.com.patientsEndpoints.model

class Room {

    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var name: String? = null
    private var totalNumberOfBeds: Int? = null
    private var unitId: Long? = null
    private var unitName: String? = null
    private var ward: String? = null
    private var wardId: Long? = null


    fun getHospital(): String? {
        return hospital
    }


    fun setHospital(hospital: String?): Room? {
        this.hospital = hospital
        return this
    }


    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?):Room? {
        this.hospitalId = hospitalId
        return this
    }


    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?):Room? {
        this.id = id
        return this
    }


    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Room? {
        this.joiningTime = joiningTime
        return this
    }

    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Room? {
        this.name = name
        return this
    }

    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }

    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Room? {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }


    fun getUnitId(): Long? {
        return unitId
    }


    fun setUnitId(unitId: Long?): Room? {
        this.unitId = unitId
        return this
    }


    fun getUnitName(): String? {
        return unitName
    }


    fun setUnitName(unitName: String?): Room? {
        this.unitName = unitName
        return this
    }


    fun getWard(): String? {
        return ward
    }


    fun setWard(ward: String?): Room? {
        this.ward = ward
        return this
    }


    fun getWardId(): Long? {
        return wardId
    }

    fun setWardId(wardId: Long?): Room? {
        this.wardId = wardId
        return this
    }



}
