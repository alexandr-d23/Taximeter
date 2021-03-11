package com.example.taximeter.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import java.util.function.DoubleConsumer
import kotlin.math.abs

@Entity
data class Job(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateOfJob: DateTime = DateTime(),
    @Embedded
    val period: Time = Time(),
    var moneySum: Double = 0.0,
    val moneyPerHour: Double = 0.0,
    var addedSum: Double = 0.0
) {
    fun addSecond() {
        with(period) {
            seconds++
            moneySum =
                (moneyPerHour * seconds) / 3600 + (moneyPerHour * minutes) / 60 + (moneyPerHour * hours)
            if (seconds == 60) {
                seconds = 0
                minutes++

                if (minutes == 60) {
                    minutes = 0
                    hours++
                }
            }
        }
    }



    fun addSum(sum: Double) {
        moneySum += sum
        addedSum += sum
    }

    fun clearAddedSum() {
        moneySum -= addedSum
        addedSum = 0.0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Job
        if (id != other.id) return false
        if (dateOfJob != other.dateOfJob) return false
        if (period != other.period) return false
        if (abs(moneySum - other.moneySum) >0.0001) return false
        if (abs(moneyPerHour - other.moneyPerHour)>0.0001) return false
        if (abs(addedSum - other.addedSum) >0.0001) return false
        return true
    }
}