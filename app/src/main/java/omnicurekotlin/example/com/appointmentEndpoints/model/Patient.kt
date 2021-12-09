
package omnicurekotlin.example.com.appointmentEndpoints.model

import java.io.Serializable

class Patient : Serializable {

    var acceptTime: Long? = null
    var address: String? = null
    var appointmentId: String? = null
    var arterialBloodPressureSystolic: Double? = null
    var athenaDeviceId: String? = null
    var bdProviderId: Long? = null
    var bdProviderName: String? = null
    var bed: String? = null
    var countryCode: String? = null
    var dischargeMessage: String? = null
    var dischargeTime: Long? = null
    var dob: Long? = null
    var dobDay: Int? = null
    var dobMonth: String? = null
    var dobYear: Int? = null
    var docBoxManagerId: String? = null
    var docBoxPatientId: String? = null
    var email: String? = null
    var fname: String? = null
    var gender: String? = null
    var heartRate: Double? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var inviteTime: Long? = null
    var joiningTime: Long? = null
    var lastMessageTime: Long? = null
    var lname: String? = null
    var name: String? = null
    var note: String? = null
    var oxygenSupplement: Boolean? = null
    var patientCondition: String? = null
    var phone: String? = null
    var picUrl: String? = null
    var rdProviderId: Long? = null
    var rdProviderName: String? = null
    var respiratoryRate: Double? = null
    var score: String? = null
    var spO2: Double? = null
    var status: String? = null
    var urgent: Boolean? = null


    fun setAcceptTime(acceptTime: Long?): Patient {
        this.acceptTime = acceptTime
        return this
    }


    fun setAddress(address: String?): Patient {
        this.address = address
        return this
    }


    fun setAppointmentId(appointmentId: String?): Patient {
        this.appointmentId = appointmentId
        return this
    }

    fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double?): Patient {
        this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
        return this
    }

    fun setAthenaDeviceId(athenaDeviceId: String?): Patient {
        this.athenaDeviceId = athenaDeviceId
        return this
    }

    fun setBdProviderId(bdProviderId: Long?): Patient {
        this.bdProviderId = bdProviderId
        return this
    }


    fun setBdProviderName(bdProviderName: String?): Patient {
        this.bdProviderName = bdProviderName
        return this
    }


    fun setBed(bed: String?): Patient {
        this.bed = bed
        return this
    }


    fun setCountryCode(countryCode: String?): Patient {
        this.countryCode = countryCode
        return this
    }


    fun setDischargeMessage(dischargeMessage: String?): Patient {
        this.dischargeMessage = dischargeMessage
        return this
    }


    fun setDischargeTime(dischargeTime: Long?): Patient {
        this.dischargeTime = dischargeTime
        return this
    }


    fun setDob(dob: Long?): Patient {
        this.dob = dob
        return this
    }


    fun setDobDay(dobDay: Int?): Patient {
        this.dobDay = dobDay
        return this
    }


    fun setDobMonth(dobMonth: String?): Patient {
        this.dobMonth = dobMonth
        return this
    }


    fun setDobYear(dobYear: Int?): Patient {
        this.dobYear = dobYear
        return this
    }


    fun setDocBoxManagerId(docBoxManagerId: String?): Patient {
        this.docBoxManagerId = docBoxManagerId
        return this
    }


    fun setDocBoxPatientId(docBoxPatientId: String?): Patient {
        this.docBoxPatientId = docBoxPatientId
        return this
    }

    fun setEmail(email: String?): Patient {
        this.email = email
        return this
    }


    fun setFname(fname: String?): Patient {
        this.fname = fname
        return this
    }


    fun setGender(gender: String?): Patient {
        this.gender = gender
        return this
    }

    fun setHeartRate(heartRate: Double?): Patient {
        this.heartRate = heartRate
        return this
    }


    fun setHospital(hospital: String?): Patient {
        this.hospital = hospital
        return this
    }


    fun setHospitalId(hospitalId: Long?): Patient {
        this.hospitalId = hospitalId
        return this
    }


    fun setId(id: Long?): Patient {
        this.id = id
        return this
    }


    fun setInviteTime(inviteTime: Long?): Patient {
        this.inviteTime = inviteTime
        return this
    }


    fun setJoiningTime(joiningTime: Long?): Patient {
        this.joiningTime = joiningTime
        return this
    }


    fun setLastMessageTime(lastMessageTime: Long?): Patient {
        this.lastMessageTime = lastMessageTime
        return this
    }


    fun setLname(lname: String?): Patient {
        this.lname = lname
        return this
    }


    fun setName(name: String?): Patient {
        this.name = name
        return this
    }


    fun setNote(note: String?): Patient {
        this.note = note
        return this
    }


    fun setOxygenSupplement(oxygenSupplement: Boolean?): Patient {
        this.oxygenSupplement = oxygenSupplement
        return this
    }


    fun setPatientCondition(patientCondition: String?): Patient {
        this.patientCondition = patientCondition
        return this
    }


    fun setPhone(phone: String?): Patient {
        this.phone = phone
        return this
    }

    fun setPicUrl(picUrl: String?): Patient {
        this.picUrl = picUrl
        return this
    }


    fun setRdProviderId(rdProviderId: Long?): Patient {
        this.rdProviderId = rdProviderId
        return this
    }


    fun setRdProviderName(rdProviderName: String?): Patient {
        this.rdProviderName = rdProviderName
        return this
    }


    fun setRespiratoryRate(respiratoryRate: Double?): Patient {
        this.respiratoryRate = respiratoryRate
        return this
    }


    fun setScore(score: String?): Patient {
        this.score = score
        return this
    }


    fun setSpO2(spO2: Double?): Patient {
        this.spO2 = spO2
        return this
    }


    fun setStatus(status: String?): Patient {
        this.status = status
        return this
    }

    fun setUrgent(urgent: Boolean?): Patient {
        this.urgent = urgent
        return this
    }
}