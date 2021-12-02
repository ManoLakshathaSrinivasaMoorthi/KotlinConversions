package omnicurekotlin.example.com.healthcareEndPoints.model

import java.io.Serializable

class AddNotificationDataRequest : Serializable {

    //com.google.api.client.util.Key
    private var id: String? = null

    //com.google.api.client.util.Key
    //    private Long providerId;
    private var userId: Long? = null

    //com.google.api.client.util.Key
    private var mobileAcuity: Boolean? = null

    //com.google.api.client.util.Key
    private var webAcuity: Boolean? = null

    //com.google.api.client.util.Key
    private var econsult: Boolean? = null

    //com.google.api.client.util.Key
    private var census: Boolean? = null

    //com.google.api.client.util.Key
    private var handoff: Boolean? = null

    //com.google.api.client.util.Key
    private var newpatient: Boolean? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
        this.id = id
    }

    fun getUserId(): Long? {
        return userId
    }

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

    fun getMobileAcuity(): Boolean? {
        return mobileAcuity
    }

    fun setMobileAcuity(mobileAcuity: Boolean?) {
        this.mobileAcuity = mobileAcuity
    }

    fun getWebAcuity(): Boolean? {
        return webAcuity
    }

    fun setWebAcuity(webAcuity: Boolean?) {
        this.webAcuity = webAcuity
    }

    //    public Boolean getAcuity() {
//        return acuity;
//    }
//
//    public void setAcuity(Boolean acuity) {
//        this.acuity = acuity;
//    }

    //    public Boolean getAcuity() {
    //        return acuity;
    //    }
    //
    //    public void setAcuity(Boolean acuity) {
    //        this.acuity = acuity;
    //    }
    fun getEconsult(): Boolean? {
        return econsult
    }

    fun setEconsult(econsult: Boolean?) {
        this.econsult = econsult
    }

    fun getCensus(): Boolean? {
        return census
    }

    fun setCensus(census: Boolean?) {
        this.census = census
    }

    fun getHandoff(): Boolean? {
        return handoff
    }

    fun setHandoff(handoff: Boolean?) {
        this.handoff = handoff
    }

    fun getNewpatient(): Boolean? {
        return newpatient
    }

    fun setNewpatient(newpatient: Boolean?) {
        this.newpatient = newpatient
    }

}