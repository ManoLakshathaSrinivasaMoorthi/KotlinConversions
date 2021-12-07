/**
 * Copyright Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kotlinomnicure.model

import omnicurekotlin.example.com.providerEndpoints.model.ChatMessageStatusModel


class HandOffList  {


    private var id: String? = null
    var messageId: String? = null
    var message: String? = null
    private var type //text,image,video,audio,system
            : String? = null
    var subType //completion, summary
            : String? = null
    private var text: String? = null
    var title: String? = null
    private var name: String? = null
    private var role: String? = null
    private var urgent = false
    var senderId: String? = null
    private var photoUrl: String? = null
    private var imageUrl: String? = null
    private var thumbUrl: String? = null
    private var videoUrl: String? = null
    private var filename: String? = null
    var senderName: String? = null
    var receiverList: List<ChatMessageStatusModel?>? = null
    var time: Long? = null
    private var patientId: Long? = null
    private var consultId: Long? = null
    var accepterName: String? = null
    private var status //Sent, Delivered, Read
            : String? = null

    fun HandOffList() {}

    fun getSenderName(): String? {
        return senderName
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setSenderName(senderName: String?) {
        this.senderName = senderName
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getSubType(): String? {
        return subType
    }

    fun setSubType(subType: String?) {
        this.subType = subType
    }

    fun getPatientId(): Long? {
        return patientId
    }

    fun setPatientId(patientId: Long?) {
        this.patientId = patientId
    }

    fun getConsultId(): Long? {
        return consultId
    }

    fun setConsultId(consultId: Long?) {
        this.consultId = consultId
    }

    fun getMessageId(): String? {
        return messageId
    }

    fun setMessageId(messageId: String?) {
        this.messageId = messageId
    }

    fun isUrgent(): Boolean {
        return urgent
    }

    fun setUrgent(urgent: Boolean) {
        this.urgent = urgent
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getRole(): String? {
        return role
    }

    fun setRole(role: String?) {
        this.role = role
    }

    fun getSenderId(): String? {
        return senderId
    }

    fun setSenderId(senderId: String?) {
        this.senderId = senderId
    }

    fun getPhotoUrl(): String? {
        return photoUrl
    }

    fun setPhotoUrl(photoUrl: String?) {
        this.photoUrl = photoUrl
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun getThumbUrl(): String? {
        return thumbUrl
    }

    fun setThumbUrl(thumbUrl: String?) {
        this.thumbUrl = thumbUrl
    }

    fun getVideoUrl(): String? {
        return videoUrl
    }

    fun setVideoUrl(videoUrl: String?) {
        this.videoUrl = videoUrl
    }

    fun getFilename(): String? {
        return filename
    }

    fun setFilename(filename: String?) {
        this.filename = filename
    }

    fun getTime(): Long? {
        return time
    }

    fun setTime(time: Long?) {
        this.time = time
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getReceiverList(): List<ChatMessageStatusModel?>? {
        return receiverList
    }

    fun setReceiverList(receiverList: List<ChatMessageStatusModel?>?) {
        this.receiverList = receiverList
    }

    fun getAccepterName(): String? {
        return accepterName
    }

    fun setAccepterName(accepterName: String?) {
        this.accepterName = accepterName
    }
}
