package com.example.taximeter.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taximeter.entities.Job

@Database(
    entities = [
        Job::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(DateTimeTypeConverter::class)
abstract class JobsDatabase : RoomDatabase() {
    abstract val jobDAO: JobDAO

    companion object{
        @Volatile
        private var instance: JobsDatabase? = null

        fun getInstance(context: Context) : JobsDatabase{
            synchronized(this){
                return instance?: Room.databaseBuilder(
                    context.applicationContext,
                    JobsDatabase::class.java,
                    "jobs_bd")
                    .build()
                    .also {
                        instance = it
                    }
            }
        }

    }

}