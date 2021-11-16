package com.example.kotlinomnicure.media

interface Packable {
    fun marshal(out: ByteBuf?): ByteBuf?
}
