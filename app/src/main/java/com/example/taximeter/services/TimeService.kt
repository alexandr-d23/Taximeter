package com.example.taximeter.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.taximeter.data.repositories.JobRepository
import com.example.taximeter.data.repositories.JobRepositoryImpl
import com.example.taximeter.data.room.JobsDatabase
import com.example.taximeter.entities.Job
import com.example.taximeter.entities.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.joda.time.MutableDateTime
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class TimeService : Service() {
    override fun onBind(intent: Intent?): IBinder? = localBinder

    private lateinit var localBinder: LocalBinder
    private lateinit var jobRepository : JobRepository

    private var job: Job? = null
    private var timer: Timer? = null
    private var status = Status.STOPPED
    private val SERVICE_ID = 10001
    private lateinit var notification : ProcessNotification

    private var listener: ServiceListener? = null

    interface ServiceListener {
        fun started(job: Job)
        fun resumed()
        fun paused()
        fun stopped()
        fun updateDateTime(job: Job)
    }

    override fun onCreate() {
        super.onCreate()
        localBinder = LocalBinder()
        jobRepository = JobRepositoryImpl(JobsDatabase.getInstance(applicationContext).jobDAO)
        notification = ProcessNotification(applicationContext)
    }

    private fun start(price: Double) {
        job = Job(moneyPerHour = price)
        Log.d("MYTAG","Service: Start")
        notifyListenerToStart()
        status = Status.CONTINUING
        startTimer()
        Log.d("MYTAG", "STATUS CONTINUING FROM START")
        startForeground(SERVICE_ID, notification.getNotification())
    }

    private fun startTimer(){
        Log.d("MYTAG", "НАЧИНАЮ ТАЙМЕР")
        timer = Timer().also {
            it.scheduleAtFixedRate(0, 1000) {
                timerTick()
            }
        }
    }


    private fun pause(){
        timer?.cancel()
        notifyListenerToPause()
        status = Status.PAUSED
        Log.d("MYTAG", "STATUS PAUSED FROM PAUSE")
    }

    private fun resume(){
        startTimer()
        notifyListenerToResume()
        Log.d("MYTAG", "STATUS CONTINUING FROM RESUME")
        status = Status.CONTINUING
    }

    private fun stop(){
        timer?.cancel()
        saveJob()
        notifyListenerToStop()
        status = Status.STOPPED
        Log.d("MYTAG", "STATUS STOPPED FROM STOP")
        stopForeground(true)
    }

    private fun saveJob(){
        CoroutineScope(Dispatchers.IO).launch {
            val resJob = job
            job = null
            resJob?.let {
                jobRepository.insertJob(it)
            }
        }
    }

    private fun timerTick() {
        Log.d("MYTAG", "ТИКАЮ")
        job?.let{
            it.addSecond()
            updateListenerTime(it)
        }
    }

    override fun onDestroy() {
        saveJob()
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {

        fun registerServiceListener(serviceListener: ServiceListener) {
            listener = serviceListener
        }

        fun unregisterServiceListener() {
            listener = null
        }

        fun startCounting(price: Double) {
            start(price)
        }

        fun pauseCounting() {
            pause()
        }

        fun resumeCounting() {
            resume()
        }

        fun stopCounting() {
            stop()
        }

        fun getStatus():Status = status

        fun getCurrentJob():Job? = job
    }

    private fun notifyListenerToStart() {
        job?.let {
            listener?.started(it)
        }
    }

    private fun notifyListenerToResume() {
        listener?.resumed()
    }

    private fun notifyListenerToPause() {
        listener?.paused()
    }

    private fun notifyListenerToStop() {
        listener?.stopped()
    }

    private fun updateListenerTime(job: Job){
        listener?.updateDateTime(job)
    }

}