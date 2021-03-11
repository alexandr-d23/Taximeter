package com.example.taximeter.ui

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.taximeter.R
import com.example.taximeter.data.repositories.JobRepository
import com.example.taximeter.data.repositories.JobRepositoryImpl
import com.example.taximeter.data.room.JobsDatabase
import com.example.taximeter.databinding.FragmentHistoryBinding
import com.example.taximeter.ui.recyclerview.JobAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: JobAdapter
    private lateinit var jobRepository : JobRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobRepository = JobRepositoryImpl(JobsDatabase.getInstance(requireContext().applicationContext).jobDAO)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter = JobAdapter(){
            Log.d("MYTAG", it.toString())
            activity?.run{
                supportFragmentManager.beginTransaction().replace(R.id.fl_container, JobInfoFragment.newInstance(it)).addToBackStack(null).commit()
            }
        }
        bindViews()
        updateList()
    }

    private fun updateList(){
        lifecycleScope.launch {
            val list = jobRepository.getJobs()
            if(list.isEmpty()){
                binding.btnClear.isEnabled = false
            }
            else {
                binding.btnClear.isEnabled = true
            }
            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }
    }

    private fun bindViews(){
        with(binding){
            rvJobs.adapter = adapter
            rvJobs.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            btnClear.setOnClickListener {
                showClearDialog()
            }
        }
    }

    private fun showClearDialog(){
        AlertDialog.Builder(requireContext()).run{
            setTitle(R.string.clear_dialog_title)
            setMessage(R.string.clear_dialog_message)
            setPositiveButton(R.string.clear_dialog_positive){ dialog: DialogInterface, _: Int ->
                clearData()
                dialog.dismiss()
            }
            setNegativeButton(R.string.clear_dialog_negative){ dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }.show()
    }

    private fun clearData(){
        lifecycleScope.launch{
            jobRepository.deleteJobs()
            updateList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}