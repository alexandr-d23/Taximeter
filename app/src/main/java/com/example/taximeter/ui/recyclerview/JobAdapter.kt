package com.example.taximeter.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taximeter.databinding.ItemJobBinding
import com.example.taximeter.entities.Job
import org.joda.time.format.DateTimeFormat
import java.util.*
import kotlin.collections.ArrayList

class JobAdapter(
    private val itemClick: (Int) -> Unit
) : ListAdapter<Job, JobAdapter.JobViewHolder>(object :
    DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean = oldItem == newItem
}) {

    inner class JobViewHolder(private val binding: ItemJobBinding, itemClick: (Int) -> Unit ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job){
            with(binding){
                tvDatetime.text = job.dateOfJob.toString(DateTimeFormat.shortDate())
                tvMoneyPerHour.text = "${job.moneyPerHour.toInt()}/час"
                tvMoneySum.text = job.moneySum.toInt().toString()
                root.setOnClickListener {
                    itemClick.invoke(job.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder = JobViewHolder(
        ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemClick)

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) = holder.bind(getItem(position))

    override fun submitList(list: List<Job>?) {
        val newList = ArrayList(list)
        super.submitList(newList)
    }


}