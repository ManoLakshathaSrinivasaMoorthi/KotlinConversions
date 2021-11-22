package com.example.kotlinomnicure.model

class ConsultMessage {

    private var id: String? = null
    private var messageId: Long? = null
    private var type //text,image,video,audio,system
            : String? = null
    private var text: String? = null
    private var name: String? = null
    private var role: String? = null
    private var urgent = false
    private var senderId: String? = null
    private var photoUrl: String? = null
    private var imageUrl: String? = null
    private var thumbUrl: String? = null
    private var videoUrl: String? = null
    private var filename: String? = null
    private var time: Long? = null
    private var status //Sent, Delivered, Read
            : String? = null

    fun ConsultMessage() {}

    fun ConsultMessage(
        text: String?, name: String?, photoUrl: String?,
        imageUrl: String?, type: String?, time: Long,
        senderId: String?, filename: String?, status: String?, role: String?, msgId: Long?
    ) {
        this.text = text
        this.name = name
        this.photoUrl = photoUrl
        this.imageUrl = imageUrl
        this.time = time
        this.type = type
        this.senderId = senderId
        this.filename = filename
        this.status = status
        this.role = role
        messageId = msgId
    }

    fun ConsultMessage(
        text: String?, name: String?, photoUrl: String?,
        imageUrl: String?, videoUrl: String?, type: String?, time: Long,
        senderId: String?, filename: String?, status: String?, role: String?, msgId: Long?
    ) {
        this.text = text
        this.name = name
        this.photoUrl = photoUrl
        this.imageUrl = imageUrl
        this.videoUrl = videoUrl
        this.time = time
        this.type = type
        this.senderId = senderId
        this.filename = filename
        this.status = status
        this.role = role
        messageId = msgId
    }


    fun getMessageId(): Long? {
        return messageId
    }

    fun setMessageId(messageId: Long?) {
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
}
