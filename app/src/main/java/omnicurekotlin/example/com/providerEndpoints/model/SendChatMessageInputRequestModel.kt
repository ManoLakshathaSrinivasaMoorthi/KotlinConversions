package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model

import java.io.Serializable


class SendChatMessageInputRequestModel : Serializable {

    var id: Long? = null
        get() = field
        set(id) {
            field = id
        }

    //com.google.api.client.util.Key
    var senderId: Int? = null

    //com.google.api.client.util.Key
    var message: String? = null

    //com.google.api.client.util.Key
    var type: String? = null

    //com.google.api.client.util.Key
    var subType: String? = null

    //com.google.api.client.util.Key
    var patientId: Long? = null

    //com.google.api.client.util.Key
    var senderName: String? = null

    //com.google.api.client.util.Key
    var urgent: Boolean? = null

    //com.google.api.client.util.Key
    var important: Boolean? = null

    //com.google.api.client.util.Key
    var token: String? = null

    //com.google.api.client.util.Key
    var title: String? = null

    //com.google.api.client.util.Key
    var time: Long? = null

    var providerId:String?=null


    @JvmName("getTime1")
    fun getTime(): Long? {
        return time
    }

    @JvmName("setTime1")
    fun setTime(time: Long?) {
        this.time = time
    }

    @JvmName("getTitle1")
    fun getTitle(): String? {
        return title
    }

    @JvmName("setTitle1")
    fun setTitle(title: String?) {
        this.title = title
    }

    @JvmName("getToken1")
    fun getToken(): String? {
        return token
    }

    @JvmName("setToken1")
    fun setToken(token: String?) {
        this.token = token
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//

    //    public Long getId() {
    //        return id;
    //    }
    //
    //    public void setId(Long id) {
    //        this.id = id;
    //    }
    //
    fun getProviderId(): String? {
        return providerId
    }

    fun setProviderId(providerId: Long?) {
        this.providerId = providerId.toString()
    }

    @JvmName("getId1")
    fun getId(): String? {
        return providerId
    }

    @JvmName("setId1")
    fun setId(id: Long?) {
        providerId = id.toString()
    }

    @JvmName("getSenderId1")
    fun getSenderId(): Int? {
        return senderId
    }

    @JvmName("setSenderId1")
    fun setSenderId(senderId: Int?) {
        this.senderId = senderId
    }

    @JvmName("getMessage1")
    fun getMessage(): String? {
        return message
    }

    @JvmName("setMessage1")
    fun setMessage(message: String?) {
        this.message = message
    }

    @JvmName("getType1")
    fun getType(): String? {
        return type
    }

    @JvmName("setType1")
    fun setType(type: String?) {
        this.type = type
    }

    @JvmName("getSubType1")
    fun getSubType(): String? {
        return subType
    }

    @JvmName("setSubType1")
    fun setSubType(subType: String?) {
        this.subType = subType
    }

    @JvmName("getPatientId1")
    fun getPatientId(): Long? {
        return patientId
    }

    @JvmName("setPatientId1")
    fun setPatientId(patientId: Long?) {
        this.patientId = patientId
    }

    @JvmName("getSenderName1")
    fun getSenderName(): String? {
        return senderName
    }

    @JvmName("setSenderName1")
    fun setSenderName(senderName: String?) {
        this.senderName = senderName
    }

    @JvmName("getUrgent1")
    fun getUrgent(): Boolean? {
        return urgent
    }

    @JvmName("setUrgent1")
    fun setUrgent(urgent: Boolean?) {
        this.urgent = urgent
    }

    @JvmName("getImportant1")
    fun getImportant(): Boolean? {
        return important
    }

    @JvmName("setImportant1")
    fun setImportant(important: Boolean?) {
        this.important = important
    }
}