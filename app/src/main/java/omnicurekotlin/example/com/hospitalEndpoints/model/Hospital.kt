package omnicurekotlin.example.com.hospitalEndpoints.model

class Hospital {


    private var address: String? = null
    private var subRegionName: String? = null
    private var bdDoctorCount: Long? = null
    private var bothDoctorCount: Long? = null
    private var city: String? = null
    private var contactDesignation: String? = null
    private var contactName: String? = null
    private var contactPhone: String? = null
    private var country: String? = null
    private var doctorCount: Long? = null
    private var email: String? = null
    private var experience: String? = null
    var id: Long? = null
    private var joiningTime: Long? = null
    private var lat: Double? = null
    private var lng: Double? = null
    var name: String? = null
    private var occupiedBeds: Int? = null
    private var phone: String? = null
    private var picUrl: String? = null
    private var pinCode: Long? = null
    private var rdDoctorCount: Long? = null
    private var searchNameInUp: String? = null
    private var state: String? = null
    private var status: String? = null
    private var street: String? = null
    private var totalNumberOfBeds: Int? = null
    private var totalNumberOfWards: Int? = null
    private var virtualHospital: Boolean? = null
    private var zipCode: String? = null
    private var zone: String? = null

    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): Hospital? {
        this.address = address
        return this
    }

    fun getBdDoctorCount(): Long? {
        return bdDoctorCount
    }

    fun setBdDoctorCount(bdDoctorCount: Long?): Hospital? {
        this.bdDoctorCount = bdDoctorCount
        return this
    }

    fun getBothDoctorCount(): Long? {
        return bothDoctorCount
    }

    fun setBothDoctorCount(bothDoctorCount: Long?): Hospital? {
        this.bothDoctorCount = bothDoctorCount
        return this
    }

    fun getCity(): String? {
        return city
    }

    fun setCity(city: String?): Hospital? {
        this.city = city
        return this
    }

    fun getSubRegionName(): String? {
        return subRegionName
    }

    fun setSubRegionName(subRegionName: String?) {
        this.subRegionName = subRegionName
    }

    fun getContactDesignation(): String? {
        return contactDesignation
    }

    fun setContactDesignation(contactDesignation: String?): Hospital? {
        this.contactDesignation = contactDesignation
        return this
    }

    fun getContactName(): String? {
        return contactName
    }


    fun setContactName(contactName: String?): Hospital? {
        this.contactName = contactName
        return this
    }

    fun getContactPhone(): String? {
        return contactPhone
    }


    fun setContactPhone(contactPhone: String?): Hospital? {
        this.contactPhone = contactPhone
        return this
    }


    fun getCountry(): String? {
        return country
    }

    fun setCountry(country: String?): Hospital? {
        this.country = country
        return this
    }

    fun getDoctorCount(): Long? {
        return doctorCount
    }

    fun setDoctorCount(doctorCount: Long?): Hospital? {
        this.doctorCount = doctorCount
        return this
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): Hospital? {
        this.email = email
        return this
    }

    fun getExperience(): String? {
        return experience
    }

    fun setExperience(experience: String?): Hospital? {
        this.experience = experience
        return this
    }

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): Hospital? {
        this.id = id
        return this
    }

    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: Long?): Hospital? {
        this.joiningTime = joiningTime
        return this
    }

    fun getLat(): Double? {
        return lat
    }

    fun setLat(lat: Double?): Hospital? {
        this.lat = lat
        return this
    }

    fun getLng(): Double? {
        return lng
    }

    fun setLng(lng: Double?): Hospital? {
        this.lng = lng
        return this
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Hospital? {
        this.name = name
        return this
    }

    fun getOccupiedBeds(): Int? {
        return occupiedBeds
    }

    fun setOccupiedBeds(occupiedBeds: Int?): Hospital? {
        this.occupiedBeds = occupiedBeds
        return this
    }


    fun getPhone(): String? {
        return phone
    }


    fun setPhone(phone: String?): Hospital? {
        this.phone = phone
        return this
    }


    fun getPicUrl(): String? {
        return picUrl
    }

    fun setPicUrl(picUrl: String?): Hospital? {
        this.picUrl = picUrl
        return this
    }


    fun getPinCode(): Long? {
        return pinCode
    }

    fun setPinCode(pinCode: Long?): Hospital? {
        this.pinCode = pinCode
        return this
    }

    fun getRdDoctorCount(): Long? {
        return rdDoctorCount
    }

    fun setRdDoctorCount(rdDoctorCount: Long?): Hospital? {
        this.rdDoctorCount = rdDoctorCount
        return this
    }


    fun getSearchNameInUp(): String? {
        return searchNameInUp
    }


    fun setSearchNameInUp(searchNameInUp: String?): Hospital? {
        this.searchNameInUp = searchNameInUp
        return this
    }

    fun getState(): String? {
        return state
    }

    fun setState(state: String?): Hospital? {
        this.state = state
        return this
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?): Hospital? {
        this.status = status
        return this
    }

    fun getStreet(): String? {
        return street
    }

    fun setStreet(street: String?): Hospital? {
        this.street = street
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getTotalNumberOfBeds(): Int? {
        return totalNumberOfBeds
    }

    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Hospital? {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    fun getTotalNumberOfWards(): Int? {
        return totalNumberOfWards
    }

    fun setTotalNumberOfWards(totalNumberOfWards: Int?): Hospital? {
        this.totalNumberOfWards = totalNumberOfWards
        return this
    }

    fun getVirtualHospital(): Boolean? {
        return virtualHospital
    }

    fun setVirtualHospital(virtualHospital: Boolean?): Hospital? {
        this.virtualHospital = virtualHospital
        return this
    }


    fun getZipCode(): String? {
        return zipCode
    }

    fun setZipCode(zipCode: String?): Hospital? {
        this.zipCode = zipCode
        return this
    }


    fun getZone(): String? {
        return zone
    }


    fun setZone(zone: String?): Hospital? {
        this.zone = zone
        return this
    }

   /* operator fun set(fieldName: String?, value: Any?): Hospital? {
        return super.set(fieldName, value) as Hospital?
    }

    fun clone(): Hospital? {
        return super.clone() as Hospital?
    }*/


}