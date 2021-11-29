package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.patientsEndpoints.model

class PatientDetail {

    var patient: Patient? = null

    private var status = false
    private var errorId = 0
    var errorMsg: String? =null


    fun getStatus(): Boolean {
        return status
    }

    fun setStatus(status: Boolean) {
        this.status = status
    }

    fun getErrorId(): Int {
        return errorId
    }

    fun setErrorId(errorId: Int) {
        this.errorId = errorId
    }

    @JvmName("setErrorMsg1")
    fun setErrorMsg(errorMsg: String) {
        this.errorMsg = errorMsg
    }

    class Patient {

        private var arterialBloodPressureSystolic = 0.0
        private var arterialBloodPressureDiastolic = 0.0
        private var respiratoryRate = 0.0
        private var spO2 = 0.0
        private var fio2 = 0.0
        private var temperature = 0.0
        private var heartRate = 0.0
        private var status: String? = null
        private var patientCondition: String? = null
        private var oxygenSupplement = false
        private var score: String? = null
        private var inviteTime: String? = null
        private var joiningTime: String? = null
        private var hospitalId: String? = null
        private var wardName: String? = null
        private var hospital: String? = null
        private var phone: String? = null
        private var dobYear = 0
        private var dobMonth: String? = null
        private var teamName: String? = null
        private var dobDay = 0
        private var dob: String? = null
        private var gender: String? = null
        private var bdProviderName: String? = null
        private var bdProviderId: String? = null
        private var note: String? = null
        private var lname: String? = null
        private var fname: String? = null
       // var name: String? = null
        private var id: String? = null
        private var recordNumber: String? = null
        private var syncTime: String? = null
        private var spO2Value: String? = null
        private var fio2Value: String? = null
        private var heartRateValue: String? = null
        private var respiratoryRateValue: String? = null
        private var arterialBloodPressureSystolicValue: String? = null
        private var arterialBloodPressureDiastolicValue: String? = null
        private var temperatureValue: String? = null
        private var covidPositive: String? = null

        fun getTeamName(): String? {
            return teamName
        }

        fun setTeamName(teamName: String?) {
            this.teamName = teamName
        }

        fun getCovidPositive(): String? {
            return covidPositive
        }

        fun setCovidPositive(covidPositive: String?) {
            this.covidPositive = covidPositive
        }

        fun getSpO2Value(): String? {
            return spO2Value
        }

        fun setSpO2Value(spO2Value: String?) {
            this.spO2Value = spO2Value
        }

        fun getFio2Value(): String? {
            return fio2Value
        }

        fun setFio2Value(fio2Value: String?) {
            this.fio2Value = fio2Value
        }

        fun getHeartRateValue(): String? {
            return heartRateValue
        }

        fun setHeartRateValue(heartRateValue: String?) {
            this.heartRateValue = heartRateValue
        }

        fun getRespiratoryRateValue(): String? {
            return respiratoryRateValue
        }

        fun setRespiratoryRateValue(respiratoryRateValue: String?) {
            this.respiratoryRateValue = respiratoryRateValue
        }

        fun getArterialBloodPressureSystolicValue(): String? {
            return arterialBloodPressureSystolicValue
        }

        fun setArterialBloodPressureSystolicValue(arterialBloodPressureSystolicValue: String?) {
            this.arterialBloodPressureSystolicValue = arterialBloodPressureSystolicValue
        }

        fun getArterialBloodPressureDiastolicValue(): String? {
            return arterialBloodPressureDiastolicValue
        }

        fun setArterialBloodPressureDiastolicValue(arterialBloodPressureDiastolicValue: String?) {
            this.arterialBloodPressureDiastolicValue = arterialBloodPressureDiastolicValue
        }

        fun getTemperatureValue(): String? {
            return temperatureValue
        }

        fun setTemperatureValue(temperatureValue: String?) {
            this.temperatureValue = temperatureValue
        }

        fun getSyncTime(): String? {
            return syncTime
        }

        fun setSyncTime(syncTime: String?) {
            this.syncTime = syncTime
        }

        fun getRecordNumber(): String? {
            return recordNumber
        }

        fun setRecordNumber(recordNumber: String?) {
            this.recordNumber = recordNumber
        }

        fun getArterialBloodPressureSystolic(): Double {
            return arterialBloodPressureSystolic
        }

        fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double) {
            this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
        }

        fun getArterialBloodPressureDiastolic(): Double {
            return arterialBloodPressureDiastolic
        }

        fun setArterialBloodPressureDiastolic(arterialBloodPressureDiastolic: Double) {
            this.arterialBloodPressureDiastolic = arterialBloodPressureDiastolic
        }

        fun getRespiratoryRate(): Double {
            return respiratoryRate
        }

        fun setRespiratoryRate(respiratoryRate: Double) {
            this.respiratoryRate = respiratoryRate
        }

        fun getSpO2(): Double {
            return spO2
        }

        fun setSpO2(spO2: Double) {
            this.spO2 = spO2
        }

        fun getFio2(): Double {
            return fio2
        }

        fun setFio2(fio2: Double) {
            this.fio2 = fio2
        }

        fun getTemperature(): Double {
            return temperature
        }

        fun setTemperature(temperature: Double) {
            this.temperature = temperature
        }

        fun getHeartRate(): Double {
            return heartRate
        }

        fun setHeartRate(heartRate: Double) {
            this.heartRate = heartRate
        }

        fun getStatus(): String? {
            return status
        }

        fun setStatus(status: String?) {
            this.status = status
        }

        fun getPatientCondition(): String? {
            return patientCondition
        }

        fun setPatientCondition(patientCondition: String?) {
            this.patientCondition = patientCondition
        }

        fun isOxygenSupplement(): Boolean {
            return oxygenSupplement
        }

        fun setOxygenSupplement(oxygenSupplement: Boolean) {
            this.oxygenSupplement = oxygenSupplement
        }

        fun getScore(): String? {
            return score
        }

        fun setScore(score: String?) {
            this.score = score
        }

        fun getInviteTime(): String? {
            return inviteTime
        }

        fun setInviteTime(inviteTime: String?) {
            this.inviteTime = inviteTime
        }

        fun getJoiningTime(): String? {
            return joiningTime
        }

        fun setJoiningTime(joiningTime: String?) {
            this.joiningTime = joiningTime
        }

        fun getHospitalId(): String? {
            return hospitalId
        }

        fun setHospitalId(hospitalId: String?) {
            this.hospitalId = hospitalId
        }

        fun getWardName(): String? {
            return wardName
        }

        fun setWardName(wardName: String?) {
            this.wardName = wardName
        }

        fun getPhone(): String? {
            return phone
        }

        fun setPhone(phone: String?) {
            this.phone = phone
        }

        fun getHospital(): String? {
            return hospital
        }

        fun setHospital(hospital: String?) {
            this.hospital = hospital
        }

        fun getDobYear(): Int {
            return dobYear
        }

        fun setDobYear(dobYear: Int) {
            this.dobYear = dobYear
        }

        fun getDobMonth(): String? {
            return dobMonth
        }

        fun setDobMonth(dobMonth: String?) {
            this.dobMonth = dobMonth
        }

        fun getDobDay(): Int {
            return dobDay
        }

        fun setDobDay(dobDay: Int) {
            this.dobDay = dobDay
        }

        fun getDob(): String? {
            return dob
        }

        fun setDob(dob: String?) {
            this.dob = dob
        }

        fun getGender(): String? {
            return gender
        }

        fun setGender(gender: String?) {
            this.gender = gender
        }

        fun getBdProviderName(): String? {
            return bdProviderName
        }

        fun setBdProviderName(bdProviderName: String?) {
            this.bdProviderName = bdProviderName
        }

        fun getBdProviderId(): String? {
            return bdProviderId
        }

        fun setBdProviderId(bdProviderId: String?) {
            this.bdProviderId = bdProviderId
        }

        fun getNote(): String? {
            return note
        }

        fun setNote(note: String?) {
            this.note = note
        }

        fun getLname(): String? {
            return lname
        }

        fun setLname(lname: String?) {
            this.lname = lname
        }

        fun getFname(): String? {
            return fname
        }

        fun setFname(fname: String?) {
            this.fname = fname
        }

     /*   fun getName(): String? {
            return name
        }

        fun setName(name: String?) {
            this.name = name
        }*/

        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }
    }
}
