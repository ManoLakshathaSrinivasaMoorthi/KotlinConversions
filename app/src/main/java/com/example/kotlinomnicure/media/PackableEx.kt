package com.example.kotlinomnicure.media

import com.example.dailytasksamplepoc.kotlinomnicure.media.ByteBuf


interface PackableEx : Packable {
    fun unmarshal(`in`: ByteBuf?)
}