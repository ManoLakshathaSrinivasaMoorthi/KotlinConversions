package omnicurekotlin.example.com.appointmentEndpoints.model

class AppointmentListResponse {

    private var appointmentList: List<Appointment?>? = null
    private var count: Int? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var status: Boolean? = null


    fun getAppointmentList(): List<Appointment?>? {
        return appointmentList
    }

    fun setAppointmentList(appointmentList: List<Appointment?>?): AppointmentListResponse? {
        this.appointmentList = appointmentList
        return this
    }


    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): AppointmentListResponse? {
        this.count = count
        return this
    }

    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): AppointmentListResponse? {
        this.errorId = errorId
        return this
    }
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): AppointmentListResponse? {
        this.errorMessage = errorMessage
        return this
    }


    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): AppointmentListResponse? {
        this.id = id
        return this
    }


    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): AppointmentListResponse? {
        this.status = status
        return this
    }



}
