package com.bluepig.tnmalarm.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bluepig.tnmalarm.databinding.ItemAlarmBinding
import com.bluepig.tnmalarm.model.Alarm

class MainAlarmAdapter(
    val rootClickListener: (Alarm) -> Unit,
    val onOffClickListener: (Alarm) -> Unit
) : RecyclerView.Adapter<MainAlarmAdapter.AlarmViewHolder>() {
    var items: MutableList<Alarm> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
    override fun getItemId(position: Int): Long = position.toLong()

    fun addItem(newItems: List<Alarm>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
    }

    inner class AlarmViewHolder(val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    rootClickListener(items[adapterPosition])
                }
            }

            binding.tvSwitch.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onOffClickListener(items[adapterPosition])
                }
            }
        }

        fun bind(item: Alarm) {
            binding.item = item
        }
    }
}