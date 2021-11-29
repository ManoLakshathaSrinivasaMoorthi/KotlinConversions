package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model

import java.io.Serializable

//public class ChatMessages extends com.google.api.client.json.GenericJson {
class ChatMessages : Serializable {
    //@com.google.api.client.util.Key
    var id: String? = null

    //@com.google.api.client.util.Key
    var senderId: String? = null

    //@com.google.api.client.util.Key
    var message: String? = null

    //@com.google.api.client.util.Key
    var type: String? = null

    //@com.google.api.client.util.Key
    var subType: String? = null

    //@com.google.api.client.util.Key
    var patientId: String? = null

    //@com.google.api.client.util.Key
    var consultId: String? = null

    //@com.google.api.client.util.Key
    var senderName: String? = null

    //@com.google.api.client.util.Key
    var time: String? = null

    //@com.google.api.client.util.Key
    var urgent: Boolean? = null

    //@com.google.api.client.util.Key
    var important: Boolean? = null

    //@com.google.api.client.util.Key
    var receiver: String? = null

    //@com.google.api.client.util.Key
    var senderProfilePic: String? = null
}