package com.example.roomdatabasewithpaging3.Adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.roomdatabasewithpaging3.Data.Dogs
import com.example.roomdatabasewithpaging3.databinding.EachRowBinding
import javax.inject.Inject

class DogsAdapter
@Inject
constructor() : PagingDataAdapter<Dogs,DogsAdapter.DogsViewHolder>(DiffUtils) {

    class DogsViewHolder(private val binding: EachRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(dogs: Dogs){
            binding.apply {
                image.load(dogs.url)
            }
        }
    }

    object DiffUtils : DiffUtil.ItemCallback<Dogs>(){
        override fun areItemsTheSame(oldItem: Dogs, newItem: Dogs): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dogs, newItem: Dogs): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: DogsViewHolder, position: Int) {
        val dogs = getItem(position)
        if(dogs != null){
            holder.bind(dogs)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogsViewHolder {
       return DogsViewHolder(EachRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}