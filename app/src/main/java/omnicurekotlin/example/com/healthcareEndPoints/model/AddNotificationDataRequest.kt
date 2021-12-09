package omnicurekotlin.example.com.healthcareEndPoints.model

import java.io.Serializable

class AddNotificationDataRequest : Serializable {

    var id: String? = null
    var userId: Long? = null
    var mobileAcuity: Boolean? = null
    var webAcuity: Boolean? = null
    var econsult: Boolean? = null
    var census: Boolean? = null
    var handoff: Boolean? = null
    var newpatient: Boolean? = null


    @JvmName("getId1")
    fun getId(): String? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: String?) {
        this.id = id
        this.id = id
    }

    @JvmName("getUserId1")
    fun getUserId(): Long? {
        return userId
    }

    @JvmName("setUserId1")
    fun setUserId(userId: Long?) {
        this.userId = userId
    }

    fun getProviderId(): Long? {
//        return providerId;
        return userId
    }

    fun setProviderId(providerId: Long?) {
//        this.providerId = providerId;
        userId = providerId
    }

    @JvmName("getMobileAcuity1")
    fun getMobileAcuity(): Boolean? {
        return mobileAcuity
    }

    @JvmName("setMobileAcuity1")
    fun setMobileAcuity(mobileAcuity: Boolean?) {
        this.mobileAcuity = mobileAcuity
    }

    @JvmName("getWebAcuity1")
    fun getWebAcuity(): Boolean? {
        return webAcuity
    }

    @JvmName("setWebAcuity1")
    fun setWebAcuity(webAcuity: Boolean?) {
        this.webAcuity = webAcuity
    }

    @JvmName("getEconsult1")
    fun getEconsult(): Boolean? {
        return econsult
    }

    @JvmName("setEconsult1")
    fun setEconsult(econsult: Boolean?) {
        this.econsult = econsult
    }

    @JvmName("getCensus1")
    fun getCensus(): Boolean? {
        return census
    }

    @JvmName("setCensus1")
    fun setCensus(census: Boolean?) {
        this.census = census
    }

    @JvmName("getHandoff1")
    fun getHandoff(): Boolean? {
        return handoff
    }

    @JvmName("setHandoff1")
    fun setHandoff(handoff: Boolean?) {
        this.handoff = handoff
    }

    @JvmName("getNewpatient1")
    fun getNewpatient(): Boolean? {
        return newpatient
    }

    @JvmName("setNewpatient1")
    fun setNewpatient(newpatient: Boolean?) {
        this.newpatient = newpatient
    }

}