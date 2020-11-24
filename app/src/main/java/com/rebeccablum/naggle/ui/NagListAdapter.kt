package com.rebeccablum.naggle.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.databinding.NagItemViewBinding
import com.rebeccablum.naggle.models.Nag

class NagListAdapter(private val viewModel: NagListViewModel) :
    ListAdapter<Nag, NagListAdapter.NagViewHolder>(NagListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NagViewHolder {
        return NagViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.nag_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NagViewHolder, position: Int) {
        val nag = getItem(position)
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { viewModel.onNagSelected(nag) }
    }

    inner class NagViewHolder(private val binding: NagItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nag: Nag) {
            binding.viewModel = NagViewModel(nag)
            binding.executePendingBindings()
        }
    }
}

private class NagListDiffCallback : DiffUtil.ItemCallback<Nag>() {
    override fun areItemsTheSame(oldItem: Nag, newItem: Nag): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Nag, newItem: Nag): Boolean {
        return oldItem == newItem
    }
}
