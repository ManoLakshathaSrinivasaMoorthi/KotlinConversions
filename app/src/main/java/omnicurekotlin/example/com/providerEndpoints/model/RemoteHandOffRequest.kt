package omnicurekotlin.example.com.providerEndpoints.model

class RemoteHandOffRequest {

    private var summaryNote: String? = null
    private var patientId: String? = null
    private var remoteProviderId = 0

    fun getSummaryNote(): String? {
        return summaryNote
    }

    fun setSummaryNote(summaryNote: String?) {
        this.summaryNote = summaryNote
    }

    fun getPatientId(): String? {
        return patientId
    }

    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    fun setRemoteProviderId(remoteProviderId: Int) {
        this.remoteProviderId = remoteProviderId
    }
}
