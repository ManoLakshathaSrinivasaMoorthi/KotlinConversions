
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.appointmentEndpoints.model
class CommonResponse {

    var activeList: List<Patient>? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */

    var dischargedCount: Int? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */

    var dischargedList: List<Patient>? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */

    var errorId: Int? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */

    var errorMessage: String? = null
        private set
    /**
     * @return value or `null` for none
     */

    var id: Long? = null

    var pendingList: List<Patient>? = null
        private set

    var provider: Provider? = null
        private set

    var providerList: List<Provider>? = null
        private set


    private var room: Room? = null

    var status: Boolean? = null
        private set

    var unit: Unit? = null
        private set

    var ward: Ward? = null
        private set


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

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): CommonResponse {
        this.id = id
        return this
    }

    /**
     * @param pendingList pendingList or `null` for none
     */
    fun setPendingList(pendingList: List<Patient>?): CommonResponse {
        this.pendingList = pendingList
        return this
    }

    /**
     * @param provider provider or `null` for none
     */
    fun setProvider(provider: Provider?): CommonResponse {
        this.provider = provider
        return this
    }

    /**
     * @param providerList providerList or `null` for none
     */
    fun setProviderList(providerList: List<Provider>?): CommonResponse {
        this.providerList = providerList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRoom():Room? {
        return room
    }

    /**
     * @param room room or `null` for none
     */
    fun setRoom(room:Room?): CommonResponse {
        this.room = room
        return this
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: Boolean?): CommonResponse {
        this.status = status
        return this
    }

    /**
     * @param unit unit or `null` for none
     */
    fun setUnit(unit: Unit?): CommonResponse {
        this.unit = unit
        return this
    }

    /**
     * @param ward ward or `null` for none
     */
    fun setWard(ward: Ward?): CommonResponse {
        this.ward = ward
        return this
    }
}