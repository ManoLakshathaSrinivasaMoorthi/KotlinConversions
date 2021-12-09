
package omnicurekotlin.example.com.appointmentEndpoints.model


class CommonResponse {

    var activeList: List<Patient>? = null
    var dischargedCount: Int? = null
    var dischargedList: List<Patient>? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null
    var pendingList: List<Patient>? = null
    var provider: Provider? = null
    var providerList: List<Provider>? = null
    var room: Room? = null
    var status: Boolean? = null
    var unit: Unit? = null
    var ward: Ward? = null


    fun setActiveList(activeList: List<Patient>?): CommonResponse {
        this.activeList = activeList
        return this
    }


    fun setDischargedCount(dischargedCount: Int?): CommonResponse {
        this.dischargedCount = dischargedCount
        return this
    }


    fun setDischargedList(dischargedList: List<Patient>?): CommonResponse {
        this.dischargedList = dischargedList
        return this
    }


    fun setErrorId(errorId: Int?): CommonResponse {
        this.errorId = errorId
        return this
    }


    fun setErrorMessage(errorMessage: String?): CommonResponse {
        this.errorMessage = errorMessage
        return this
    }


    fun setId(id: Long?): CommonResponse {
        this.id = id
        return this
    }

    fun setPendingList(pendingList: List<Patient>?): CommonResponse {
        this.pendingList = pendingList
        return this
    }


    fun setProvider(provider: Provider?): CommonResponse {
        this.provider = provider
        return this
    }

    fun setProviderList(providerList: List<Provider>?): CommonResponse {
        this.providerList = providerList
        return this
    }


    @JvmName("getRoom1")
    fun getRoom(): Room? {
        return room
    }


    fun setRoom(room: Room?): CommonResponse {
        this.room = room
        return this
    }


    fun setStatus(status: Boolean?): CommonResponse {
        this.status = status
        return this
    }

    fun setUnit(unit: Unit?): CommonResponse {
        this.unit = unit
        return this
    }


    fun setWard(ward: Ward?): CommonResponse {
        this.ward = ward
        return this
    }
}