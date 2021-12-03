package omnicurekotlin.example.com.providerEndpoints.model

class CommonResponse {


    private var activeList: List<Patient?>? = null
    private var dischargedCount: Int? = null

    private var dischargedList: List<Patient?>? = null

    private var errorId: Int? = null

    var errorMessage: String? = null

    private val eNotesActivity: List<ENotesList>? = null

    private var title: String? = null

    private var id: Long? = null


    private var pendingList: List<Patient?>? = null


    private var provider: Provider? = null


    private var providerList: List<Provider?>? = null


    private var room: Room? = null


    var status: Boolean? = null

    var auditId:String?=null


    private var unit: Unit? = null


    private var ward: Ward? = null




    fun getErrorMessage(): String? {
        return errorMessage
    }

    /**
     * @param errorMessage errorMessage or `null` for none
     */
    fun setErrorMessage(errorMessage: String?): CommonResponse {
        this.errorMessage = errorMessage
        return this
    }
    fun getActiveList(): List<Patient?>? {
        return activeList
    }


    fun setActiveList(activeList: List<Patient?>?):CommonResponse {
        this.activeList = activeList
        return this
    }


    fun getDischargedCount(): Int? {
        return dischargedCount
    }


    fun setDischargedCount(dischargedCount: Int?):CommonResponse {
        this.dischargedCount = dischargedCount
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDischargedList(): List<Patient?>? {
        return dischargedList
    }
    fun geteNotesActivity(): List<ENotesList?>? {
        return eNotesActivity
    }

   /* fun getAuditId(): String? {
        return auditId
    }

    fun setAuditId(auditId: String) {
        auditId = auditId
    }*/
    /**
     * @param dischargedList dischargedList or `null` for none
     */
    fun setDischargedList(dischargedList: List<Patient?>?): CommonResponse {
        this.dischargedList = dischargedList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): CommonResponse {
        this.errorId = errorId
        return this
    }

/*
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?):CommonResponse? {
        this.errorMessage = errorMessage
        return this
    }*/

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?):CommonResponse {
        this.id = id
        return this
    }


    fun getPendingList(): List<Patient?>? {
        return pendingList
    }

    fun setPendingList(pendingList: List<Patient?>?): CommonResponse {
        this.pendingList = pendingList
        return this
    }


    fun getProvider(): Provider? {
        return provider
    }


    fun setProvider(provider: Provider?): CommonResponse {
        this.provider = provider
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getProviderList(): List<Provider?>? {
        return providerList
    }

    /**
     * @param providerList providerList or `null` for none
     */
    fun setProviderList(providerList: List<Provider?>?):CommonResponse {
        this.providerList = providerList
        return this
    }

    fun getRoom(): Room? {
        return room
    }


    fun setRoom(room: Room?): CommonResponse {
        this.room = room
        return this
    }



  /*  fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?):CommonResponse? {
        this.status = status
        return this
    }*/


    fun getUnit(): Unit? {
        return unit
    }


    fun setUnit(unit: Unit): CommonResponse {
        this.unit = unit
        return this
    }


    fun getWard(): Ward? {
        return ward
    }


    fun setWard(ward: Ward?): CommonResponse {
        this.ward = ward
        return this


}

}