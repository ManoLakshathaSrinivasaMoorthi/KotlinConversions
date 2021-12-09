package omnicurekotlin.example.com.appointmentEndpoints.model

class AppointmentListResponse {

     var appointmentList: List<Appointment?>? = null
     var count: Int? = null
     var errorId: Int? = null
     var errorMessage: String? = null
     var id: Long? = null
     var status: Boolean? = null


    @JvmName("getAppointmentList1")
    fun getAppointmentList(): List<Appointment?>? {
        return appointmentList
    }

    fun setAppointmentList(appointmentList: List<Appointment?>?): AppointmentListResponse {
        this.appointmentList = appointmentList
        return this
    }


    @JvmName("getCount1")
    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): AppointmentListResponse {
        this.count = count
        return this
    }

    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): AppointmentListResponse {
        this.errorId = errorId
        return this
    }
    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): AppointmentListResponse {
        this.errorMessage = errorMessage
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): AppointmentListResponse {
        this.id = id
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): AppointmentListResponse {
        this.status = status
        return this
    }



}
