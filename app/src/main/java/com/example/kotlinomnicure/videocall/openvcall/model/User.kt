package com.example.kotlinomnicure.videocall.openvcall.model

class User {
    constructor(uid: Int, name: String?) {
        this.uid = uid
        this.name = name
    }

    var uid = 0
    var name: String? = null
}
