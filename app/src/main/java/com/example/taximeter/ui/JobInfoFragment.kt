package com.example.taximeter.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.taximeter.data.repositories.JobRepository
import com.example.taximeter.data.repositories.JobRepositoryImpl
import com.example.taximeter.data.room.JobsDatabase
import com.example.taximeter.databinding.FragmentJobInfoBinding
import com.example.taximeter.entities.Job
import kotlinx.coroutines.launch
import org.joda.time.format.DateTimeFormat

class JobInfoFragment : Fragment() {
    private var _binding: FragmentJobInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var receivedJob: Job
    private lateinit var job: Job
    private lateinit var jobRepository: JobRepository


    companion object {
        const val jobIndex = "JOB_ID"

        fun newInstance(jobId: Int): JobInfoFragment {
            val args = Bundle()
            args.putInt(jobIndex, jobId)
            val fragment = JobInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobRepository =
            JobRepositoryImpl(JobsDatabase.getInstance(requireContext().applicationContext).jobDAO)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobInfoBinding.inflate(inflater, container, false)
        lifecycleScope.launchWhenCreated {
            arguments?.getInt(jobIndex)?.let {
                receivedJob = jobRepository.getJobById(it)
                job = receivedJob.copy()
                bindViews()
            }
        }
        return binding.root
    }

    private fun setTextInputError(text: String){
        binding.tiAddMoney.error = text
    }

    private fun bindViews() {
        with(binding) {
            tvDate.text = getJobDateTimeText()
            tvResultTime.text = job.period.toString()
            btnDeleteAdded.setOnClickListener {
                job.clearAddedSum()
                updateSumText()
            }
            etAddMoney.setOnKeyListener { _, keyCode, event ->
                if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    etAddMoney.clearFocus()
                    btnAddMoney.performClick()
                }
                false
            }
            btnDelete.setOnClickListener {
                AlertDialog.Builder(requireContext()).run{
                    setTitle("Удалить?")
                    setMessage("Текущий элемент будет удалён")
                    setPositiveButton("Да"){ dialog: DialogInterface, _: Int ->
                        lifecycleScope.launch {
                            jobRepository.deleteJobById(job.id)
                        }
                        btnBack.performClick()
                        dialog.dismiss()
                    }
                    setNegativeButton("Нет"){ dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    }
                }.show()
            }
            btnAddMoney.setOnClickListener {
                if(etAddMoney.text?.isEmpty() != false){
                    setTextInputError("Введи число")
                }
                else{
                    job.addSum(etAddMoney.text.toString().toDouble())
                    etAddMoney.setText("")
                    etAddMoney.clearFocus()
                    updateSumText()
                }
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch {
                    jobRepository.updateJob(job)
                    receivedJob = job.copy()
                }
                btnSave.isEnabled = false
            }
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
        }
        updateSumText()
    }

    private fun updateSumText(){
        with(binding){
            tvMoneyAdded.text = job.addedSum.toInt().toString()
            tvMoneySum.text = job.moneySum.toInt().toString()
            btnSave.isEnabled = job != receivedJob
            Log.d("MYAG",  (job != receivedJob).toString())
            Log.d("MYAG",  job.toString())
            Log.d("MYAG",  receivedJob.toString())
        }
    }

    private fun getJobDateTimeText():String {
        val date = job.dateOfJob.toString(DateTimeFormat.shortDate())
        val time = job.dateOfJob.toString(DateTimeFormat.shortTime())
        return "$date $time"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}