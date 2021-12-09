package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class Bed:Serializable {

    var bd: String? = null
    var bdId: Long? = null
    var contactDesignation: String? = null
    var contactName: String? = null
    var contactPhone: String? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var joiningTime: Long? = null
    var name: String? = null
    var patient: String? = null
    var patientId: Long? = null
    var ward: String? = null
    var wardId: Long? = null


    @JvmName("getBd1")
    fun getBd(): String? {
        return bd
    }


    fun setBd(bd: String?): Bed {
        this.bd = bd
        return this
    }


    @JvmName("getBdId1")
    fun getBdId(): Long? {
        return bdId
    }

    fun setBdId(bdId: Long?): Bed {
        this.bdId = bdId
        return this
    }

    @JvmName("getContactDesignation1")
    fun getContactDesignation(): String? {
        return contactDesignation
    }

    fun setContactDesignation(contactDesignation: String?): Bed {
        this.contactDesignation = contactDesignation
        return this
    }


    @JvmName("getContactName1")
    fun getContactName(): String? {
        return contactName
    }

    fun setContactName(contactName: String?): Bed {
        this.contactName = contactName
        return this
    }

    @JvmName("getContactPhone1")
    fun getContactPhone(): String? {
        return contactPhone
    }

    fun setContactPhone(contactPhone: String?): Bed {
        this.contactPhone = contactPhone
        return this
    }


    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }


    fun setHospital(hospital: String?): Bed {
        this.hospital = hospital
        return this
    }


    @JvmName("getHospitalId1")
    fun getHospitalId(): Long? {
        return hospitalId
    }


    fun setHospitalId(hospitalId: Long?): Bed {
        this.hospitalId = hospitalId
        return this
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): Bed {
        this.id = id
        return this
    }


    @JvmName("getJoiningTime1")
    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Bed {
        this.joiningTime = joiningTime
        return this
    }


    @JvmName("getName1")
    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Bed {
        this.name = name
        return this
    }

    @JvmName("getPatient1")
    fun getPatient(): String? {
        return patient
    }


    fun setPatient(patient: String?): Bed {
        this.patient = patient
        return this
    }


    @JvmName("getPatientId1")
    fun getPatientId(): Long? {
        return patientId
    }


    fun setPatientId(patientId: Long?): Bed {
        this.patientId = patientId
        return this
    }


    @JvmName("getWard1")
    fun getWard(): String? {
        return ward
    }


    fun setWard(ward: String?): Bed {
        this.ward = ward
        return this
    }


    @JvmName("getWardId1")
    fun getWardId(): Long? {
        return wardId
    }

    fun setWardId(wardId: Long?): Bed {
        this.wardId = wardId
        return this
    }



}