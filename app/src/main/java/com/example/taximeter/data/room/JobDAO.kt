package com.example.taximeter.data.room

import androidx.room.*
import com.example.taximeter.entities.Job

@Dao
interface JobDAO {

    @Insert
    suspend fun insertJob(job: Job)

    @Insert
    suspend fun insertJobs(list: List<Job>)

    @Update
    suspend fun updateJob(job: Job)

    @Query("SELECT * FROM Job ORDER BY dateOfJob DESC")
    suspend fun getJobs(): List<Job>

    @Query("SELECT * FROM Job WHERE id = :id")
    suspend fun getJobById(id: Int): List<Job>

    @Query("DELETE FROM Job")
    suspend fun deleteJobs()

    @Query("DELETE FROM Job WHERE id = :id")
    suspend fun deleteJobById(id: Int)
}