package omnicurekotlin.example.com.patientsEndpoints.model

class AthenaDeviceData {

    private var batteryLife: Int? = null
    private var deviceID: Int? = null
    private var deviceID1: Int? = null
    private var deviceTypeValue: Int? = null
    private var diastolicBP: Int? = null
    private var direction: Int? = null
    private var fname: String? = null
    private var hRpR: Int? = null
    private var hardwareId: String? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var lname: String? = null
    private var medicalID: Int? = null
    private var name: String? = null
    private var packetType: Int? = null
    private var patientID: Int? = null
    private var respiration: Int? = null
    private var sequenceNumber: Int? = null
    private var spO2: Int? = null
    private var status: String? = null
    private var systolicBP: Int? = null
    private var takingBp: Int? = null
    private var temperature: Int? = null
    private var updateTime: Long? = null
    private var wrapperTypeValue: Int? = null

    fun getBatteryLife(): Int? {
        return batteryLife
    }


    fun setBatteryLife(batteryLife: Int?): AthenaDeviceData? {
        this.batteryLife = batteryLife
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDeviceID(): Int? {
        return deviceID
    }

    /**
     * @param deviceID deviceID or `null` for none
     */
    fun setDeviceID(deviceID: Int?): AthenaDeviceData? {
        this.deviceID = deviceID
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDeviceID1(): Int? {
        return deviceID1
    }

    /**
     * @param deviceID1 deviceID1 or `null` for none
     */
    fun setDeviceID1(deviceID1: Int?): AthenaDeviceData? {
        this.deviceID1 = deviceID1
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDeviceTypeValue(): Int? {
        return deviceTypeValue
    }

    /**
     * @param deviceTypeValue deviceTypeValue or `null` for none
     */
    fun setDeviceTypeValue(deviceTypeValue: Int?): AthenaDeviceData? {
        this.deviceTypeValue = deviceTypeValue
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDiastolicBP(): Int? {
        return diastolicBP
    }

    /**
     * @param diastolicBP diastolicBP or `null` for none
     */
    fun setDiastolicBP(diastolicBP: Int?): AthenaDeviceData? {
        this.diastolicBP = diastolicBP
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDirection(): Int? {
        return direction
    }

    /**
     * @param direction direction or `null` for none
     */
    fun setDirection(direction: Int?): AthenaDeviceData? {
        this.direction = direction
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
    fun setFname(fname: String?): AthenaDeviceData? {
        this.fname = fname
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHRpR(): Int? {
        return hRpR
    }

    /**
     * @param hRpR hRpR or `null` for none
     */
    fun setHRpR(hRpR: Int?): AthenaDeviceData? {
        this.hRpR = hRpR
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHardwareId(): String? {
        return hardwareId
    }

    /**
     * @param hardwareId hardwareId or `null` for none
     */
    fun setHardwareId(hardwareId: String?): AthenaDeviceData? {
        this.hardwareId = hardwareId
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
    fun setId(id: Long?): AthenaDeviceData? {
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
    fun setJoiningTime(joiningTime: Long?): AthenaDeviceData? {
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
    fun setLname(lname: String?): AthenaDeviceData? {
        this.lname = lname
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getMedicalID(): Int? {
        return medicalID
    }

    /**
     * @param medicalID medicalID or `null` for none
     */
    fun setMedicalID(medicalID: Int?): AthenaDeviceData? {
        this.medicalID = medicalID
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
    fun setName(name: String?): AthenaDeviceData? {
        this.name = name
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPacketType(): Int? {
        return packetType
    }

    /**
     * @param packetType packetType or `null` for none
     */
    fun setPacketType(packetType: Int?): AthenaDeviceData? {
        this.packetType = packetType
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPatientID(): Int? {
        return patientID
    }

    /**
     * @param patientID patientID or `null` for none
     */
    fun setPatientID(patientID: Int?): AthenaDeviceData? {
        this.patientID = patientID
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRespiration(): Int? {
        return respiration
    }

    /**
     * @param respiration respiration or `null` for none
     */
    fun setRespiration(respiration: Int?): AthenaDeviceData? {
        this.respiration = respiration
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getSequenceNumber(): Int? {
        return sequenceNumber
    }

    /**
     * @param sequenceNumber sequenceNumber or `null` for none
     */
    fun setSequenceNumber(sequenceNumber: Int?): AthenaDeviceData? {
        this.sequenceNumber = sequenceNumber
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getSpO2(): Int? {
        return spO2
    }

    /**
     * @param spO2 spO2 or `null` for none
     */
    fun setSpO2(spO2: Int?): AthenaDeviceData? {
        this.spO2 = spO2
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
    fun setStatus(status: String?): AthenaDeviceData? {
        this.status = status
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getSystolicBP(): Int? {
        return systolicBP
    }

    /**
     * @param systolicBP systolicBP or `null` for none
     */
    fun setSystolicBP(systolicBP: Int?): AthenaDeviceData? {
        this.systolicBP = systolicBP
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getTakingBp(): Int? {
        return takingBp
    }

    /**
     * @param takingBp takingBp or `null` for none
     */
    fun setTakingBp(takingBp: Int?): AthenaDeviceData? {
        this.takingBp = takingBp
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getTemperature(): Int? {
        return temperature
    }

    /**
     * @param temperature temperature or `null` for none
     */
    fun setTemperature(temperature: Int?): AthenaDeviceData? {
        this.temperature = temperature
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getUpdateTime(): Long? {
        return updateTime
    }

    /**
     * @param updateTime updateTime or `null` for none
     */
    fun setUpdateTime(updateTime: Long?): AthenaDeviceData? {
        this.updateTime = updateTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getWrapperTypeValue(): Int? {
        return wrapperTypeValue
    }

    /**
     * @param wrapperTypeValue wrapperTypeValue or `null` for none
     */
    fun setWrapperTypeValue(wrapperTypeValue: Int?): AthenaDeviceData? {
        this.wrapperTypeValue = wrapperTypeValue
        return this
    }


}


