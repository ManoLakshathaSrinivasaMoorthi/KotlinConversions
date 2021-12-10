package omnicurekotlin.example.com.patientsEndpoints.model

class PatientDetail {

    var patient: Patient? = null
    var status = false
    var errorId = 0
    var errorMsg: String? =null


    @JvmName("getStatus1")
    fun getStatus(): Boolean {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean) {
        this.status = status
    }

    @JvmName("getErrorId1")
    fun getErrorId(): Int {
        return errorId
    }

    @JvmName("setErrorId1")
    fun setErrorId(errorId: Int) {
        this.errorId = errorId
    }

    @JvmName("setErrorMsg1")
    fun setErrorMsg(errorMsg: String) {
        this.errorMsg = errorMsg
    }

    class Patient {

         var arterialBloodPressureSystolic = 0.0
         var arterialBloodPressureDiastolic = 0.0
         var respiratoryRate = 0.0
         var spO2 = 0.0
         var fio2 = 0.0
         var temperature = 0.0
         var heartRate = 0.0
         var status: String? = null
         var patientCondition: String? = null
         var oxygenSupplement = false
         var score: String? = null
         var inviteTime: String? = null
         var joiningTime: String? = null
         var hospitalId: String? = null
         var wardName: String? = null
         var hospital: String? = null
         var phone: String? = null
         var dobYear = 0
         var dobMonth: String? = null
         var teamName: String? = null
         var dobDay = 0
         var dob: String? = null
         var gender: String? = null
         var bdProviderName: String? = null
         var bdProviderId: String? = null
         var note: String? = null
         var lname: String? = null
         var fname: String? = null
         var name: String? = null
         var id: String? = null
         var recordNumber: String? = null
         var syncTime: String? = null
         var spO2Value: String? = null
         var fio2Value: String? = null
         var heartRateValue: String? = null
         var respiratoryRateValue: String? = null
         var arterialBloodPressureSystolicValue: String? = null
         var arterialBloodPressureDiastolicValue: String? = null
         var temperatureValue: String? = null
         var covidPositive: String? = null

        @JvmName("getTeamName1")
        fun getTeamName(): String? {
            return teamName
        }

        @JvmName("setTeamName1")
        fun setTeamName(teamName: String?) {
            this.teamName = teamName
        }

        @JvmName("getCovidPositive1")
        fun getCovidPositive(): String? {
            return covidPositive
        }

        @JvmName("setCovidPositive1")
        fun setCovidPositive(covidPositive: String?) {
            this.covidPositive = covidPositive
        }

        @JvmName("getSpO2Value1")
        fun getSpO2Value(): String? {
            return spO2Value
        }

        @JvmName("setSpO2Value1")
        fun setSpO2Value(spO2Value: String?) {
            this.spO2Value = spO2Value
        }

        @JvmName("getFio2Value1")
        fun getFio2Value(): String? {
            return fio2Value
        }

        @JvmName("setFio2Value1")
        fun setFio2Value(fio2Value: String?) {
            this.fio2Value = fio2Value
        }

        @JvmName("getHeartRateValue1")
        fun getHeartRateValue(): String? {
            return heartRateValue
        }

        @JvmName("setHeartRateValue1")
        fun setHeartRateValue(heartRateValue: String?) {
            this.heartRateValue = heartRateValue
        }

        @JvmName("getRespiratoryRateValue1")
        fun getRespiratoryRateValue(): String? {
            return respiratoryRateValue
        }

        @JvmName("setRespiratoryRateValue1")
        fun setRespiratoryRateValue(respiratoryRateValue: String?) {
            this.respiratoryRateValue = respiratoryRateValue
        }

        @JvmName("getArterialBloodPressureSystolicValue1")
        fun getArterialBloodPressureSystolicValue(): String? {
            return arterialBloodPressureSystolicValue
        }

        @JvmName("setArterialBloodPressureSystolicValue1")
        fun setArterialBloodPressureSystolicValue(arterialBloodPressureSystolicValue: String?) {
            this.arterialBloodPressureSystolicValue = arterialBloodPressureSystolicValue
        }

        @JvmName("getArterialBloodPressureDiastolicValue1")
        fun getArterialBloodPressureDiastolicValue(): String? {
            return arterialBloodPressureDiastolicValue
        }

        @JvmName("setArterialBloodPressureDiastolicValue1")
        fun setArterialBloodPressureDiastolicValue(arterialBloodPressureDiastolicValue: String?) {
            this.arterialBloodPressureDiastolicValue = arterialBloodPressureDiastolicValue
        }

        @JvmName("getTemperatureValue1")
        fun getTemperatureValue(): String? {
            return temperatureValue
        }

        @JvmName("setTemperatureValue1")
        fun setTemperatureValue(temperatureValue: String?) {
            this.temperatureValue = temperatureValue
        }

        @JvmName("getSyncTime1")
        fun getSyncTime(): String? {
            return syncTime
        }

        @JvmName("setSyncTime1")
        fun setSyncTime(syncTime: String?) {
            this.syncTime = syncTime
        }

        @JvmName("getRecordNumber1")
        fun getRecordNumber(): String? {
            return recordNumber
        }

        @JvmName("setRecordNumber1")
        fun setRecordNumber(recordNumber: String?) {
            this.recordNumber = recordNumber
        }

        @JvmName("getArterialBloodPressureSystolic1")
        fun getArterialBloodPressureSystolic(): Double {
            return arterialBloodPressureSystolic
        }

        @JvmName("setArterialBloodPressureSystolic1")
        fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double) {
            this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
        }

        @JvmName("getArterialBloodPressureDiastolic1")
        fun getArterialBloodPressureDiastolic(): Double {
            return arterialBloodPressureDiastolic
        }

        @JvmName("setArterialBloodPressureDiastolic1")
        fun setArterialBloodPressureDiastolic(arterialBloodPressureDiastolic: Double) {
            this.arterialBloodPressureDiastolic = arterialBloodPressureDiastolic
        }

        @JvmName("getRespiratoryRate1")
        fun getRespiratoryRate(): Double {
            return respiratoryRate
        }

        @JvmName("setRespiratoryRate1")
        fun setRespiratoryRate(respiratoryRate: Double) {
            this.respiratoryRate = respiratoryRate
        }

        @JvmName("getSpO21")
        fun getSpO2(): Double {
            return spO2
        }

        @JvmName("setSpO21")
        fun setSpO2(spO2: Double) {
            this.spO2 = spO2
        }

        @JvmName("getFio21")
        fun getFio2(): Double {
            return fio2
        }

        @JvmName("setFio21")
        fun setFio2(fio2: Double) {
            this.fio2 = fio2
        }

        @JvmName("getTemperature1")
        fun getTemperature(): Double {
            return temperature
        }

        @JvmName("setTemperature1")
        fun setTemperature(temperature: Double) {
            this.temperature = temperature
        }

        @JvmName("getHeartRate1")
        fun getHeartRate(): Double {
            return heartRate
        }

        @JvmName("setHeartRate1")
        fun setHeartRate(heartRate: Double) {
            this.heartRate = heartRate
        }

        @JvmName("getStatus1")
        fun getStatus(): String? {
            return status
        }

        @JvmName("setStatus1")
        fun setStatus(status: String?) {
            this.status = status
        }

        @JvmName("getPatientCondition1")
        fun getPatientCondition(): String? {
            return patientCondition
        }

        @JvmName("setPatientCondition1")
        fun setPatientCondition(patientCondition: String?) {
            this.patientCondition = patientCondition
        }

        fun isOxygenSupplement(): Boolean {
            return oxygenSupplement
        }

        @JvmName("setOxygenSupplement1")
        fun setOxygenSupplement(oxygenSupplement: Boolean) {
            this.oxygenSupplement = oxygenSupplement
        }

        @JvmName("getScore1")
        fun getScore(): String? {
            return score
        }

        @JvmName("setScore1")
        fun setScore(score: String?) {
            this.score = score
        }

        @JvmName("getInviteTime1")
        fun getInviteTime(): String? {
            return inviteTime
        }

        @JvmName("setInviteTime1")
        fun setInviteTime(inviteTime: String?) {
            this.inviteTime = inviteTime
        }

        @JvmName("getJoiningTime1")
        fun getJoiningTime(): String? {
            return joiningTime
        }

        @JvmName("setJoiningTime1")
        fun setJoiningTime(joiningTime: String?) {
            this.joiningTime = joiningTime
        }

        @JvmName("getHospitalId1")
        fun getHospitalId(): String? {
            return hospitalId
        }

        @JvmName("setHospitalId1")
        fun setHospitalId(hospitalId: String?) {
            this.hospitalId = hospitalId
        }

        @JvmName("getWardName1")
        fun getWardName(): String? {
            return wardName
        }

        @JvmName("setWardName1")
        fun setWardName(wardName: String?) {
            this.wardName = wardName
        }

        @JvmName("getPhone1")
        fun getPhone(): String? {
            return phone
        }

        @JvmName("setPhone1")
        fun setPhone(phone: String?) {
            this.phone = phone
        }

        @JvmName("getHospital1")
        fun getHospital(): String? {
            return hospital
        }

        @JvmName("setHospital1")
        fun setHospital(hospital: String?) {
            this.hospital = hospital
        }

        @JvmName("getDobYear1")
        fun getDobYear(): Int {
            return dobYear
        }

        @JvmName("setDobYear1")
        fun setDobYear(dobYear: Int) {
            this.dobYear = dobYear
        }

        @JvmName("getDobMonth1")
        fun getDobMonth(): String? {
            return dobMonth
        }

        @JvmName("setDobMonth1")
        fun setDobMonth(dobMonth: String?) {
            this.dobMonth = dobMonth
        }

        @JvmName("getDobDay1")
        fun getDobDay(): Int {
            return dobDay
        }

        @JvmName("setDobDay1")
        fun setDobDay(dobDay: Int) {
            this.dobDay = dobDay
        }

        @JvmName("getDob1")
        fun getDob(): String? {
            return dob
        }

        @JvmName("setDob1")
        fun setDob(dob: String?) {
            this.dob = dob
        }

        @JvmName("getGender1")
        fun getGender(): String? {
            return gender
        }

        @JvmName("setGender1")
        fun setGender(gender: String?) {
            this.gender = gender
        }

        @JvmName("getBdProviderName1")
        fun getBdProviderName(): String? {
            return bdProviderName
        }

        @JvmName("setBdProviderName1")
        fun setBdProviderName(bdProviderName: String?) {
            this.bdProviderName = bdProviderName
        }

        @JvmName("getBdProviderId1")
        fun getBdProviderId(): String? {
            return bdProviderId
        }

        @JvmName("setBdProviderId1")
        fun setBdProviderId(bdProviderId: String?) {
            this.bdProviderId = bdProviderId
        }

        @JvmName("getNote1")
        fun getNote(): String? {
            return note
        }

        @JvmName("setNote1")
        fun setNote(note: String?) {
            this.note = note
        }

        @JvmName("getLname1")
        fun getLname(): String? {
            return lname
        }

        @JvmName("setLname1")
        fun setLname(lname: String?) {
            this.lname = lname
        }

        @JvmName("getFname1")
        fun getFname(): String? {
            return fname
        }

        @JvmName("setFname1")
        fun setFname(fname: String?) {
            this.fname = fname
        }



        @JvmName("getId1")
        fun getId(): String? {
            return id
        }

        @JvmName("setId1")
        fun setId(id: String?) {
            this.id = id
        }
    }
}
