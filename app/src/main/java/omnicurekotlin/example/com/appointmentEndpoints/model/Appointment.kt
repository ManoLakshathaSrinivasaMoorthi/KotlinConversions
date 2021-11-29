package omnicurekotlin.example.com.appointmentEndpoints.model

class Appointment {

    private var address: String? = null
    private var countryCode: String? = null
    private var dob: Long? = null
    private var dobDay: Int? = null
    private var dobMonth: String? = null
    private var dobYear: Int? = null
    private var email: String? = null
    private var isFamilyMember: Boolean? = null
    private var memFirstName: String? = null
    private var memLastName: String? = null
    private var relationship: String? = null
    private var password: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var lname: String? = null
    private var name: String? = null
    private var note: String? = null
    private var phone: String? = null
    private var status: String? = null


    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): Appointment? {
        this.address = address
        return this
    }


    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): Appointment? {
        this.countryCode = countryCode
        return this
    }


    fun getDob(): Long? {
        return dob
    }

    fun setDob(dob: Long?): Appointment? {
        this.dob = dob
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDobDay(): Int? {
        return dobDay
    }

    fun setDobDay(dobDay: Int?): Appointment? {
        this.dobDay = dobDay
        return this
    }

    fun getDobMonth(): String? {
        return dobMonth
    }

    fun setDobMonth(dobMonth: String?): Appointment? {
        this.dobMonth = dobMonth
        return this
    }

    fun getDobYear(): Int? {
        return dobYear
    }

    fun setDobYear(dobYear: Int?): Appointment? {
        this.dobYear = dobYear
        return this
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): Appointment? {
        this.email = email
        return this
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getFamilyMember(): Boolean? {
        return isFamilyMember
    }

    fun setFamilyMember(familyMember: Boolean?) {
        isFamilyMember = familyMember
    }

    fun getMemFirstName(): String? {
        return memFirstName
    }

    fun setMemFirstName(memFirstName: String?) {
        this.memFirstName = memFirstName
    }

    fun getMemLastName(): String? {
        return memLastName
    }

    fun setMemLastName(memLastName: String?) {
        this.memLastName = memLastName
    }

    fun getRelationship(): String? {
        return relationship
    }

    fun setRelationship(relationship: String?) {
        this.relationship = relationship
    }

    fun getFname(): String? {
        return fname
    }


    fun setFname(fname: String?): Appointment? {
        this.fname = fname
        return this
    }

    fun getGender(): String? {
        return gender
    }


    fun setGender(gender: String?): Appointment? {
        this.gender = gender
        return this
    }

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): Appointment? {
        this.id = id
        return this
    }

    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: Long?): Appointment? {
        this.joiningTime = joiningTime
        return this
    }

    fun getLname(): String? {
        return lname
    }

    fun setLname(lname: String?): Appointment? {
        this.lname = lname
        return this
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Appointment? {
        this.name = name
        return this
    }

    fun getNote(): String? {
        return note
    }

    fun setNote(note: String?): Appointment? {
        this.note = note
        return this
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?): Appointment? {
        this.phone = phone
        return this
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?): Appointment? {
        this.status = status
        return this
    }

    constructor(fieldName: String?, value: Any?): super() {
        return
    }

    constructor() :super() {
        return
    }

    private var providerId: Long? = null

    private var token: String? = null

    fun getProviderId(): Long? {
        return providerId
    }

    fun setProviderId(providerId: Long?) {
        this.providerId = providerId
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }


}
