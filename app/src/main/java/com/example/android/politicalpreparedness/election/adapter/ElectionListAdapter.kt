package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemElectionBinding
import com.example.android.politicalpreparedness.databinding.ItemRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeViewHolder

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {


    //TODO: Add companion object to inflate ViewHolder (from)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(private var binding: ItemElectionBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup) : ElectionViewHolder {
                return ElectionViewHolder(ItemElectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

    fun bind(data: Election, clickListener: ElectionListener) {
        binding.apply {
            election = data
            onClickListener = clickListener
        }
    }
}

//TODO: Create ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }
}

//TODO: Create ElectionListener
class ElectionListener(private val data: (Election) -> Unit) {
    fun onItemClick(election: Election) = data(election)
}