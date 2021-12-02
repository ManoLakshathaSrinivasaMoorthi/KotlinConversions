package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class Ward :Serializable{

    private var contactDesignation: String? = null
    private var contactName: String? = null
    private var contactPhone: String? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var name: String? = null
    private var occupiedBeds: Int? = null
    private var totalNumberOfBeds: Int? = null


    fun getContactDesignation(): String? {
        return contactDesignation
    }


    fun setContactDesignation(contactDesignation: String?):Ward {
        this.contactDesignation = contactDesignation
        return this
    }


    fun getContactName(): String? {
        return contactName
    }


    fun setContactName(contactName: String?): Ward {
        this.contactName = contactName
        return this
    }


    fun getContactPhone(): String? {
        return contactPhone
    }


    fun setContactPhone(contactPhone: String?): Ward {
        this.contactPhone = contactPhone
        return this
    }


    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): Ward {
        this.hospital = hospital
        return this
    }


    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?): Ward {
        this.hospitalId = hospitalId
        return this
    }


    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Ward {
        this.id = id
        return this
    }


    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: Long?): Ward {
        this.joiningTime = joiningTime
        return this
    }


    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Ward {
        this.name = name
        return this
    }


    fun getOccupiedBeds(): Int? {
        return occupiedBeds
    }


    fun setOccupiedBeds(occupiedBeds: Int?): Ward {
        this.occupiedBeds = occupiedBeds
        return this
    }


    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }


    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Ward {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

//  @Override
//  public Ward set(String fieldName, Object value) {
//    return (Ward) super.set(fieldName, value);
//  }
//
//  @Override
//  public Ward clone() {
//    return (Ward) super.clone();
//  }

}
