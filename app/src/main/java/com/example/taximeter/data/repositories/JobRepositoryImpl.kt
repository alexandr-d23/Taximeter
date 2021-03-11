package com.example.taximeter.data.repositories

import android.util.Log
import com.example.taximeter.data.room.JobDAO
import com.example.taximeter.data.room.JobsDatabase
import com.example.taximeter.entities.Job

class JobRepositoryImpl(
    private val jobDAO: JobDAO
) : JobRepository {

    override suspend fun insertJob(job: Job) = jobDAO.insertJob(job)

    override suspend fun insertJobs(list: List<Job>) = jobDAO.insertJobs(list)

    override suspend fun getJobs(): List<Job> = jobDAO.getJobs()

    override suspend fun deleteJobs() = jobDAO.deleteJobs()

    override suspend fun getJobById(jobId: Int) = jobDAO.getJobById(jobId).also{ Log.d("MYTAG", it.size.toString())}[0]

    override suspend fun updateJob(job: Job) = jobDAO.updateJob(job)

    override suspend fun deleteJobById(id: Int) = jobDAO.deleteJobById(id)
}