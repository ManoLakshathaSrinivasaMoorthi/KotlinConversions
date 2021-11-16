package omnicurekotlin.example.com.patientsEndpoints.model

class HospitalList {

    private var id: String? = null
    private var name: String? = null
    private var searchNameInUp: String? = null
    private var email: String? = null
    private var joiningTime: String? = null
    private var totalNumberOfUnits: Int? = null
    private var totalNumberOfWards: Int? = null
    private var totalNumberOfRooms: Int? = null
    private var totalNumberOfBeds: Int? = null
    private var occupiedBeds: Int? = null
    private var type: String? = null
    private var ahaDesignation: String? = null
    private var regionId: String? = null
    private var subRegionId: String? = null
    private var wards: String? = null
    private var status: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getSearchNameInUp(): String? {
        return searchNameInUp
    }

    fun setSearchNameInUp(searchNameInUp: String?) {
        this.searchNameInUp = searchNameInUp
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getJoiningTime(): String? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: String?) {
        this.joiningTime = joiningTime
    }

    fun getTotalNumberOfUnits(): Int? {
        return totalNumberOfUnits
    }

    fun setTotalNumberOfUnits(totalNumberOfUnits: Int?) {
        this.totalNumberOfUnits = totalNumberOfUnits
    }

    fun getTotalNumberOfWards(): Int? {
        return totalNumberOfWards
    }

    fun setTotalNumberOfWards(totalNumberOfWards: Int?) {
        this.totalNumberOfWards = totalNumberOfWards
    }

    fun getTotalNumberOfRooms(): Int? {
        return totalNumberOfRooms
    }

    fun setTotalNumberOfRooms(totalNumberOfRooms: Int?) {
        this.totalNumberOfRooms = totalNumberOfRooms
    }

    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }

    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?) {
        this.totalNumberOfBeds = totalNumberOfBeds
    }

    fun getOccupiedBeds(): Int? {
        return occupiedBeds
    }

    fun setOccupiedBeds(occupiedBeds: Int?) {
        this.occupiedBeds = occupiedBeds
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getAhaDesignation(): String? {
        return ahaDesignation
    }

    fun setAhaDesignation(ahaDesignation: String?) {
        this.ahaDesignation = ahaDesignation
    }

    fun getRegionId(): String? {
        return regionId
    }

    fun setRegionId(regionId: String?) {
        this.regionId = regionId
    }

    fun getSubRegionId(): String? {
        return subRegionId
    }

    fun setSubRegionId(subRegionId: String?) {
        this.subRegionId = subRegionId
    }

    fun getWards(): String? {
        return wards
    }

    fun setWards(wards: String?) {
        this.wards = wards
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }
}