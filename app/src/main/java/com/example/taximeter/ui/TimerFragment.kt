package com.example.taximeter.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.taximeter.databinding.FragmentTimerBinding
import com.example.taximeter.entities.Job
import com.example.taximeter.services.Status
import com.example.taximeter.services.TimeService
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TimerFragment : Fragment(), TimeService.ServiceListener {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private var binderService: TimeService.LocalBinder? = null

    companion object {
        fun newInstance(): TimerFragment {
            val args = Bundle()
            val fragment = TimerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.startService(Intent(requireContext(), TimeService::class.java))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val myIntent = Intent(requireContext(), TimeService::class.java)
        activity?.bindService(myIntent, myConnection, Context.BIND_AUTO_CREATE)
        bindListeners()
    }

    private fun bindListeners(){
        with(binding) {
            btnStart.setOnClickListener {
                onStartClick()
            }
            btnPause.setOnClickListener {
                onPauseClick()
            }
            btnContinue.setOnClickListener {
                onResumeClick()
            }
            btnFinish.setOnClickListener {
                onStopClick()
            }
        }
    }

    private fun setTextInputError(text: String){
        binding.tiMoneyPerHour.error = text
    }

    private fun changeView(status: Status){
        when(status){
            Status.STOPPED -> {
                clearBindView()
            }
            Status.CONTINUING -> {
                continuingBindView()
            }
            Status.PAUSED -> {
                continuingBindView()
                takePause()
            }
        }
    }

    private val myConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binderService = p1 as? TimeService.LocalBinder
            binderService?.let{
                it.registerServiceListener(this@TimerFragment)
                changeView(it.getStatus())
                it.getCurrentJob()?.let {job->
                    updateTimeAndMoney(job)
                }
            }
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            binderService = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        binderService?.unregisterServiceListener()
    }

    private fun clearBindView(){
        with(binding){
            tiMoneyPerHour.visibility = View.VISIBLE
            etMoneyPerHour.setOnKeyListener { _, keyCode, event ->
                if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    etMoneyPerHour.clearFocus()
                    btnStart.performClick()
                }
                false
            }
            btnStart.visibility = View.VISIBLE
            tvTime.visibility = View.INVISIBLE
            btnPause.visibility = View.INVISIBLE
            btnContinue.visibility = View.INVISIBLE
            btnFinish.visibility = View.INVISIBLE
            tvIncome.visibility = View.INVISIBLE
            tvMoneyPerHourRes.visibility = View.INVISIBLE
            tvMoneyPerHourRes.text = ""
            etMoneyPerHour.setText("")
        }
    }

    private fun continuingBindView(){
        with(binding){
            tiMoneyPerHour.visibility = View.INVISIBLE
            btnStart.visibility = View.INVISIBLE
            tvTime.visibility = View.VISIBLE
            btnPause.visibility = View.VISIBLE
            btnContinue.visibility = View.INVISIBLE
            btnFinish.visibility = View.VISIBLE
            tvIncome.visibility = View.VISIBLE
            tvMoneyPerHourRes.visibility = View.VISIBLE
        }
    }

    private fun takePause(){
        with(binding) {
            btnPause.visibility = View.INVISIBLE
            btnContinue.visibility = View.VISIBLE
        }
    }

    private fun resume(){
        with(binding) {
            btnPause.visibility = View.VISIBLE
            btnContinue.visibility = View.INVISIBLE
        }
    }

    override fun started(job: Job) {
        Log.d("MYTAG", "${Thread.currentThread().name}")
        lifecycleScope.launch {
            Log.d("MYTAG", "${Thread.currentThread().name}")
            continuingBindView()
                binding.tvMoneyPerHourRes.text = "${job.moneyPerHour}/час"
            updateTimeAndMoney(job)
        }
    }


    override fun resumed() {
        lifecycleScope.launch {
            resume()
        }

    }

    override fun paused() {
        lifecycleScope.launch {
            takePause()
        }
    }

    override fun stopped() {
       lifecycleScope.launch {
           clearBindView()
       }
    }

    override fun updateDateTime(job: Job) {
        CoroutineScope(Dispatchers.Main).launch {
            updateTimeAndMoney(job)
        }
    }

    private fun onStartClick() {
        Log.d("TAG", "Start click")
        if(binding.etMoneyPerHour.text.toString().isEmpty()){
            setTextInputError("Введи число")
        }
        else {
            binderService?.startCounting(binding.etMoneyPerHour.text.toString().toDouble())
        }
    }

    private fun onPauseClick() {
        binderService?.pauseCounting()
    }

    private fun onResumeClick() {
        binderService?.resumeCounting()
    }

    private fun onStopClick() {
        binderService?.stopCounting()
    }

    private fun updateTimeAndMoney(job: Job) {
        with(binding) {
            tvTime.text = job.period.toString()
            tvIncome.text = job.moneySum.toInt().toString()
        }
    }

}