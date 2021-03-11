package com.example.taximeter.entities

data class Time (
    var minutes: Int = 0,
    var seconds: Int = 0,
    var hours: Int = 0
){

    override fun toString(): String {
        val stringHours = if(hours>=10)"$hours" else "0$hours"
        val stringMinutes = if(minutes>=10)"$minutes" else "0$minutes"
        val stringSeconds = if(seconds>=10)"$seconds" else "0$seconds"
        return "$stringHours:$stringMinutes:$stringSeconds"
    }
}