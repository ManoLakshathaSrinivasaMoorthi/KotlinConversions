package com.example.kotlinomnicure.media


interface PackableEx : Packable {
    fun unmarshal(`in`: ByteBuf?)
}