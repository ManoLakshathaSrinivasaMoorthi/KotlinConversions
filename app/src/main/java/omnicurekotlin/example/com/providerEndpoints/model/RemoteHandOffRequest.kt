package omnicurekotlin.example.com.providerEndpoints.model

class RemoteHandOffRequest {

     var summaryNote: String? = null
     var patientId: String? = null
     var remoteProviderId = 0

    @JvmName("getSummaryNote1")
    fun getSummaryNote(): String? {
        return summaryNote
    }

    @JvmName("setSummaryNote1")
    fun setSummaryNote(summaryNote: String?) {
        this.summaryNote = summaryNote
    }

    @JvmName("getPatientId1")
    fun getPatientId(): String? {
        return patientId
    }

    @JvmName("setPatientId1")
    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    @JvmName("setRemoteProviderId1")
    fun setRemoteProviderId(remoteProviderId: Int) {
        this.remoteProviderId = remoteProviderId
    }
}
