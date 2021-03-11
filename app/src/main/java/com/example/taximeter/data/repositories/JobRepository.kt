package com.example.taximeter.data.repositories

import com.example.taximeter.entities.Job

interface JobRepository {
    suspend fun insertJob(job: Job)
    suspend fun insertJobs(list: List<Job>)
    suspend fun getJobs():List<Job>
    suspend fun deleteJobs()
    suspend fun getJobById(jobId:Int):Job
    suspend fun updateJob(job: Job)
    suspend fun deleteJobById(id: Int)
}