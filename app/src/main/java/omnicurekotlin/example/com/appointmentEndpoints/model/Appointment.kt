package omnicurekotlin.example.com.appointmentEndpoints.model

class Appointment {

    var address: String? = null
    var countryCode: String? = null
    var dob: Long? = null
    var dobDay: Int? = null
    var dobMonth: String? = null
    var dobYear: Int? = null
    var email: String? = null
    var isFamilyMember: Boolean? = null
    var memFirstName: String? = null
    var memLastName: String? = null
    var relationship: String? = null
    var password: String? = null
    var fname: String? = null
    var gender: String? = null
    var id: Long? = null
    var joiningTime: Long? = null
    var lname: String? = null
    var name: String? = null
    var note: String? = null
    var phone: String? = null
    var status: String? = null


    @JvmName("getAddress1")
    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): Appointment {
        this.address = address
        return this
    }


    @JvmName("getCountryCode1")
    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): Appointment {
        this.countryCode = countryCode
        return this
    }


    @JvmName("getDob1")
    fun getDob(): Long? {
        return dob
    }

    fun setDob(dob: Long?): Appointment {
        this.dob = dob
        return this
    }


    @JvmName("getDobDay1")
    fun getDobDay(): Int? {
        return dobDay
    }

    fun setDobDay(dobDay: Int?): Appointment {
        this.dobDay = dobDay
        return this
    }

    @JvmName("getDobMonth1")
    fun getDobMonth(): String? {
        return dobMonth
    }

    fun setDobMonth(dobMonth: String?): Appointment {
        this.dobMonth = dobMonth
        return this
    }

    @JvmName("getDobYear1")
    fun getDobYear(): Int? {
        return dobYear
    }

    fun setDobYear(dobYear: Int?): Appointment {
        this.dobYear = dobYear
        return this
    }

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): Appointment {
        this.email = email
        return this
    }

    @JvmName("getPassword1")
    fun getPassword(): String? {
        return password
    }

    @JvmName("setPassword1")
    fun setPassword(password: String?) {
        this.password = password
    }

    fun getFamilyMember(): Boolean? {
        return isFamilyMember
    }

    @JvmName("setFamilyMember1")
    fun setFamilyMember(familyMember: Boolean?) {
        isFamilyMember = familyMember
    }

    @JvmName("getMemFirstName1")
    fun getMemFirstName(): String? {
        return memFirstName
    }

    @JvmName("setMemFirstName1")
    fun setMemFirstName(memFirstName: String?) {
        this.memFirstName = memFirstName
    }

    @JvmName("getMemLastName1")
    fun getMemLastName(): String? {
        return memLastName
    }

    @JvmName("setMemLastName1")
    fun setMemLastName(memLastName: String?) {
        this.memLastName = memLastName
    }

    @JvmName("getRelationship1")
    fun getRelationship(): String? {
        return relationship
    }

    @JvmName("setRelationship1")
    fun setRelationship(relationship: String?) {
        this.relationship = relationship
    }

    @JvmName("getFname1")
    fun getFname(): String? {
        return fname
    }


    fun setFname(fname: String?): Appointment {
        this.fname = fname
        return this
    }

    @JvmName("getGender1")
    fun getGender(): String? {
        return gender
    }


    fun setGender(gender: String?): Appointment {
        this.gender = gender
        return this
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): Appointment {
        this.id = id
        return this
    }

    @JvmName("getJoiningTime1")
    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: Long?): Appointment {
        this.joiningTime = joiningTime
        return this
    }

    @JvmName("getLname1")
    fun getLname(): String? {
        return lname
    }

    fun setLname(lname: String?): Appointment {
        this.lname = lname
        return this
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Appointment {
        this.name = name
        return this
    }

    @JvmName("getNote1")
    fun getNote(): String? {
        return note
    }

    fun setNote(note: String?): Appointment {
        this.note = note
        return this
    }

    @JvmName("getPhone1")
    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?): Appointment {
        this.phone = phone
        return this
    }

    @JvmName("getStatus1")
    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?): Appointment {
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
