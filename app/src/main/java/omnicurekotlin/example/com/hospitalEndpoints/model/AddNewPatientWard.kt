package omnicurekotlin.example.com.hospitalEndpoints.model

class AddNewPatientWard {

    var wardName: String? = null
    var bedCount: Int? = null

    @JvmName("getWardName1")
    fun getWardName(): String? {
        return wardName
    }

    @JvmName("setWardName1")
    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    @JvmName("getBedCount1")
    fun getBedCount(): Int? {
        return bedCount
    }

    @JvmName("setBedCount1")
    fun setBedCount(bedCount: Int?) {
        this.bedCount = bedCount
    }
}
