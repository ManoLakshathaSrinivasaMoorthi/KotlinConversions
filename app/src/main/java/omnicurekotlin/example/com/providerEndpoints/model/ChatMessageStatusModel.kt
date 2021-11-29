package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model

import java.io.Serializable

//public class ChatMessageStatusModel extends com.google.api.client.json.GenericJson {
class ChatMessageStatusModel  //    static {
//        // hack to force ProGuard to consider ChatMessage used, since otherwise it would be stripped out
//        // see https://github.com/google/google-api-java-client/issues/543
//        com.google.api.client.util.Data.nullOf(ChatMessages.class);
//    }
    : Serializable {
    //    @Override
    //    public String toString() {
    //        return "ChatMessageStatusModel{" +
    //                "receiverId='" + receiverId + '\'' +
    //                ", providerName='" + providerName + '\'' +
    //                ", providerType='" + providerType + '\'' +
    //                ", profilePicUrl='" + profilePicUrl + '\'' +
    //                ", status='" + status + '\'' +
    //                ", time='" + time + '\'' +
    //                '}';
    //    }
    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var receiverId: String? = null

    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var providerName: String? = null

    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var providerType: String? = null

    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var profilePicUrl: String? = null

    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var status: String? = null
    var time: Long = 0
}