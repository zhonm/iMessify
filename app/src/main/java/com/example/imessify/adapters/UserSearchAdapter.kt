package com.example.imessify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imessify.R
import com.example.imessify.databinding.ItemUserSearchBinding
import com.example.imessify.models.User
import com.bumptech.glide.Glide

class UserSearchAdapter(
    private val userClickListener: OnUserClickListener
) : ListAdapter<User, UserSearchAdapter.UserViewHolder>(UserDiffCallback()) {

    interface OnUserClickListener {
        fun onUserClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, userClickListener)
    }

    class UserViewHolder(private val binding: ItemUserSearchBinding) : 
            RecyclerView.ViewHolder(binding.root) {
        
        fun bind(user: User, listener: OnUserClickListener) {
            binding.apply {
                userName.text = user.username
                // Use email if available, otherwise show a default message
                userEmail.text = user.email ?: "No email available"
                
                // Load profile image if available
                if (user.profileImage != null) {
                    Glide.with(root.context)
                        .load(user.profileImage)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(userImage)
                } else {
                    // Set default image
                    userImage.setImageResource(R.drawable.ic_launcher_foreground)
                }
                
                // Set click listener
                root.setOnClickListener {
                    listener.onUserClick(user)
                }
            }
        }
    }

    private class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}