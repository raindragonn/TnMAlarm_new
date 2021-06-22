package com.bluepig.tnmalarm.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bluepig.tnmalarm.databinding.ItemSearchBinding
import com.bluepig.tnmalarm.model.SongResponse

class SearchAdapter(val itemClickListener : (SongResponse) -> Unit) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var items: MutableList<SongResponse> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItem(newItems: List<SongResponse>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
    }

    override fun getItemCount(): Int = items.size
    override fun getItemId(position: Int): Long = position.toLong()

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener(items[adapterPosition])
                }
            }
        }

        fun bind(item: SongResponse) {
            binding.item = item
        }
    }
}