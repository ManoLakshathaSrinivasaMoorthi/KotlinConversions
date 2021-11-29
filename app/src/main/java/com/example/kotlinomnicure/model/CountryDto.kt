package com.example.dailytasksamplepoc.kotlinomnicure.model

import com.example.dailytasksamplepoc.kotlinomnicure.model.CountryDto
import android.graphics.Bitmap

class CountryDto : Comparable<CountryDto> {
    var countryName = ""
    var flagName = ""
    var countryCode = ""
    var flagImage: Bitmap? = null
    override fun toString(): String {
        return countryName
    }

    override fun equals(objects: Any?): Boolean {
        if (objects === this) {
            return true
        }
        if (objects !is CountryDto) {
            return false
        }
        val countryDto = objects
        return countryName === countryDto.countryName || countryName != null && countryName == countryDto.countryName
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = (prime * result
                + if (countryName == null) 0 else countryName.hashCode())
        return result
    }

    override fun compareTo(countryDto: CountryDto): Int {
        return countryName.compareTo(countryDto.countryName)
    }
}