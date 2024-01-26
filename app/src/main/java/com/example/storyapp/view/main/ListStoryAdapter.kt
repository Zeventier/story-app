package com.example.storyapp.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter :
    PagingDataAdapter<Story, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }

        holder.itemView.setOnClickListener {
            if (data != null) {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.photoImageView)
            binding.apply {
                nameTextView.text = data.name
                descTextView.text = data.description
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(storyDetail: Story)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}