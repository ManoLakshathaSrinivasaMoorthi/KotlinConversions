package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class Ward :Serializable{

    var contactDesignation: String? = null
    var contactName: String? = null
    var contactPhone: String? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var joiningTime: Long? = null
    var name: String? = null
    var occupiedBeds: Int? = null
    var totalNumberOfBeds: Int? = null


    @JvmName("getContactDesignation1")
    fun getContactDesignation(): String? {
        return contactDesignation
    }


    fun setContactDesignation(contactDesignation: String?):Ward {
        this.contactDesignation = contactDesignation
        return this
    }


    @JvmName("getContactName1")
    fun getContactName(): String? {
        return contactName
    }


    fun setContactName(contactName: String?): Ward {
        this.contactName = contactName
        return this
    }


    @JvmName("getContactPhone1")
    fun getContactPhone(): String? {
        return contactPhone
    }


    fun setContactPhone(contactPhone: String?): Ward {
        this.contactPhone = contactPhone
        return this
    }


    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): Ward {
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


    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Ward {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }



}
