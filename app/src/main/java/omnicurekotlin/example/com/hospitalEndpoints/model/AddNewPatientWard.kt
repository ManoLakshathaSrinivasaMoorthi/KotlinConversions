package omnicurekotlin.example.com.hospitalEndpoints.model

class AddNewPatientWard {

    private var wardName: String? = null
    private var bedCount: Int? = null

    fun getWardName(): String? {
        return wardName
    }

    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    fun getBedCount(): Int? {
        return bedCount
    }

    fun setBedCount(bedCount: Int?) {
        this.bedCount = bedCount
    }
}
