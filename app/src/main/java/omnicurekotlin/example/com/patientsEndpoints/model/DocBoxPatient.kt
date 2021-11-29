package omnicurekotlin.example.com.patientsEndpoints.model

class DocBoxPatient {

    private var address: String? = null
    private var bdProviderId: Long? = null
    private var bdProviderName: String? = null
    private var bed: String? = null
    private var countryCode: String? = null
    private var dischargeTime: Long? = null
    private var dob: Long? = null
    private var docBoxManagerId: String? = null
    private var docBoxPatientId: String? = null
    private var email: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var hospital: String? = null
    private var hospitalDesciption: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var lname: String? = null
    private var name: String? = null
    private var note: String? = null
    private var phone: String? = null
    private var picUrl: String? = null
    private var rdProviderId: Long? = null
    private var rdProviderName: String? = null
    private var status: String? = null
    private var ward: String? = null

    fun getAddress(): String? {
        return address
    }

    /**
     * @param address address or `null` for none
     */
    fun setAddress(address: String?): DocBoxPatient? {
        this.address = address
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getBdProviderId(): Long? {
        return bdProviderId
    }

    /**
     * @param bdProviderId bdProviderId or `null` for none
     */
    fun setBdProviderId(bdProviderId: Long?): DocBoxPatient? {
        this.bdProviderId = bdProviderId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getBdProviderName(): String? {
        return bdProviderName
    }

    /**
     * @param bdProviderName bdProviderName or `null` for none
     */
    fun setBdProviderName(bdProviderName: String?): DocBoxPatient? {
        this.bdProviderName = bdProviderName
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getBed(): String? {
        return bed
    }

    /**
     * @param bed bed or `null` for none
     */
    fun setBed(bed: String?): DocBoxPatient? {
        this.bed = bed
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getCountryCode(): String? {
        return countryCode
    }

    /**
     * @param countryCode countryCode or `null` for none
     */
    fun setCountryCode(countryCode: String?): DocBoxPatient? {
        this.countryCode = countryCode
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDischargeTime(): Long? {
        return dischargeTime
    }

    /**
     * @param dischargeTime dischargeTime or `null` for none
     */
    fun setDischargeTime(dischargeTime: Long?): DocBoxPatient? {
        this.dischargeTime = dischargeTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDob(): Long? {
        return dob
    }

    /**
     * @param dob dob or `null` for none
     */
    fun setDob(dob: Long?): DocBoxPatient? {
        this.dob = dob
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDocBoxManagerId(): String? {
        return docBoxManagerId
    }

    /**
     * @param docBoxManagerId docBoxManagerId or `null` for none
     */
    fun setDocBoxManagerId(docBoxManagerId: String?): DocBoxPatient? {
        this.docBoxManagerId = docBoxManagerId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDocBoxPatientId(): String? {
        return docBoxPatientId
    }

    /**
     * @param docBoxPatientId docBoxPatientId or `null` for none
     */
    fun setDocBoxPatientId(docBoxPatientId: String?): DocBoxPatient? {
        this.docBoxPatientId = docBoxPatientId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getEmail(): String? {
        return email
    }

    /**
     * @param email email or `null` for none
     */
    fun setEmail(email: String?): DocBoxPatient? {
        this.email = email
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getFname(): String? {
        return fname
    }

    /**
     * @param fname fname or `null` for none
     */
    fun setFname(fname: String?): DocBoxPatient? {
        this.fname = fname
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getGender(): String? {
        return gender
    }

    /**
     * @param gender gender or `null` for none
     */
    fun setGender(gender: String?): DocBoxPatient? {
        this.gender = gender
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHospital(): String? {
        return hospital
    }

    /**
     * @param hospital hospital or `null` for none
     */
    fun setHospital(hospital: String?): DocBoxPatient? {
        this.hospital = hospital
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHospitalDesciption(): String? {
        return hospitalDesciption
    }

    /**
     * @param hospitalDesciption hospitalDesciption or `null` for none
     */
    fun setHospitalDesciption(hospitalDesciption: String?): DocBoxPatient? {
        this.hospitalDesciption = hospitalDesciption
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHospitalId(): Long? {
        return hospitalId
    }

    /**
     * @param hospitalId hospitalId or `null` for none
     */
    fun setHospitalId(hospitalId: Long?): DocBoxPatient? {
        this.hospitalId = hospitalId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getId(): Long? {
        return id
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): DocBoxPatient? {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getJoiningTime(): Long? {
        return joiningTime
    }

    /**
     * @param joiningTime joiningTime or `null` for none
     */
    fun setJoiningTime(joiningTime: Long?): DocBoxPatient? {
        this.joiningTime = joiningTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getLname(): String? {
        return lname
    }

    /**
     * @param lname lname or `null` for none
     */
    fun setLname(lname: String?): DocBoxPatient? {
        this.lname = lname
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getName(): String? {
        return name
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): DocBoxPatient? {
        this.name = name
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getNote(): String? {
        return note
    }

    /**
     * @param note note or `null` for none
     */
    fun setNote(note: String?): DocBoxPatient? {
        this.note = note
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPhone(): String? {
        return phone
    }

    /**
     * @param phone phone or `null` for none
     */
    fun setPhone(phone: String?): DocBoxPatient? {
        this.phone = phone
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPicUrl(): String? {
        return picUrl
    }

    /**
     * @param picUrl picUrl or `null` for none
     */
    fun setPicUrl(picUrl: String?): DocBoxPatient? {
        this.picUrl = picUrl
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRdProviderId(): Long? {
        return rdProviderId
    }

    /**
     * @param rdProviderId rdProviderId or `null` for none
     */
    fun setRdProviderId(rdProviderId: Long?): DocBoxPatient? {
        this.rdProviderId = rdProviderId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRdProviderName(): String? {
        return rdProviderName
    }

    /**
     * @param rdProviderName rdProviderName or `null` for none
     */
    fun setRdProviderName(rdProviderName: String?): DocBoxPatient? {
        this.rdProviderName = rdProviderName
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getStatus(): String? {
        return status
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: String?): DocBoxPatient? {
        this.status = status
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getWard(): String? {
        return ward
    }

    /**
     * @param ward ward or `null` for none
     */
    fun setWard(ward: String?): DocBoxPatient? {
        this.ward = ward
        return this
    }





}