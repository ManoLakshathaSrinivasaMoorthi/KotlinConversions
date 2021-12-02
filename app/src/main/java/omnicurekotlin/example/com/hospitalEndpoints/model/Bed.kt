package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class Bed:Serializable {

    private var bd: String? = null
    private var bdId: Long? = null
    private var contactDesignation: String? = null
    private var contactName: String? = null
    private var contactPhone: String? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var name: String? = null
    private var patient: String? = null
    private var patientId: Long? = null
    private var ward: String? = null
    private var wardId: Long? = null


    fun getBd(): String? {
        return bd
    }


    fun setBd(bd: String?): Bed {
        this.bd = bd
        return this
    }


    fun getBdId(): Long? {
        return bdId
    }

    fun setBdId(bdId: Long?): Bed {
        this.bdId = bdId
        return this
    }

    fun getContactDesignation(): String? {
        return contactDesignation
    }

    fun setContactDesignation(contactDesignation: String?): Bed {
        this.contactDesignation = contactDesignation
        return this
    }


    fun getContactName(): String? {
        return contactName
    }

    fun setContactName(contactName: String?): Bed {
        this.contactName = contactName
        return this
    }

    fun getContactPhone(): String? {
        return contactPhone
    }

    fun setContactPhone(contactPhone: String?): Bed {
        this.contactPhone = contactPhone
        return this
    }


    fun getHospital(): String? {
        return hospital
    }


    fun setHospital(hospital: String?): Bed {
        this.hospital = hospital
        return this
    }


    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?): Bed {
        this.hospitalId = hospitalId
        return this
    }

    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Bed {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Bed {
        this.joiningTime = joiningTime
        return this
    }


    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Bed {
        this.name = name
        return this
    }

    fun getPatient(): String? {
        return patient
    }


    fun setPatient(patient: String?): Bed {
        this.patient = patient
        return this
    }


    fun getPatientId(): Long? {
        return patientId
    }


    fun setPatientId(patientId: Long?): Bed {
        this.patientId = patientId
        return this
    }


    fun getWard(): String? {
        return ward
    }


    fun setWard(ward: String?): Bed? {
        this.ward = ward
        return this
    }


    fun getWardId(): Long? {
        return wardId
    }

    fun setWardId(wardId: Long?): Bed? {
        this.wardId = wardId
        return this
    }

//  @Override
//  public Bed set(String fieldName, Object value) {
//    return (Bed) super.set(fieldName, value);
//  }
//
//  @Override
//  public Bed clone() {
//    return (Bed) super.clone();
//  }


}