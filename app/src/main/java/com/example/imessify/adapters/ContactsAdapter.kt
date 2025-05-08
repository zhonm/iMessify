package com.example.imessify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imessify.databinding.ItemContactBinding
import com.example.imessify.models.Contact
import com.bumptech.glide.Glide

class ContactsAdapter(
    private val contactClickListener: OnContactClickListener
) : ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(ContactDiffCallback()) {

    interface OnContactClickListener {
        fun onContactClick(contact: Contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact, contactClickListener)
    }

    class ContactViewHolder(private val binding: ItemContactBinding) : 
            RecyclerView.ViewHolder(binding.root) {
        
        fun bind(contact: Contact, listener: OnContactClickListener) {
            binding.apply {
                contactName.text = contact.name
                contactPhone.text = "@${contact.username}"
                
                // Load image if available
                if (contact.imageUrl != null) {
                    Glide.with(root.context)
                        .load(contact.imageUrl)
                        .into(contactImage)
                }
                
                // Set click listener
                root.setOnClickListener {
                    listener.onContactClick(contact)
                }
            }
        }
    }

    private class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}
