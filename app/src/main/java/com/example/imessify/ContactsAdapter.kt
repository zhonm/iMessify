package com.example.imessify

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

// Renamed from UserAdapter to ContactModel
data class ContactModel(
    var name: String,
    var last_Message: String,
    var last_Msg_time: String,
    var phone_no: String,
    var image_id: Int,
    var id: Int = -1
)

class ContactsAdapter(
    private val context: Context
) : ListAdapter<ContactModel, ContactsAdapter.ViewHolder>(ContactDiffCallback()) {

    private var onItemLongClickListener: ((position: Int, contact: ContactModel) -> Unit)? = null

    fun setOnItemLongClickListener(listener: (position: Int, contact: ContactModel) -> Unit) {
        onItemLongClickListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactImage: ImageView = itemView.findViewById(R.id.contact_image)
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val contactPhone: TextView = itemView.findViewById(R.id.contact_phone)
        val callButton: ImageButton = itemView.findViewById(R.id.call_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position)

        // Use Glide to load images efficiently
        Glide.with(context)
            .load(contact.image_id)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .dontAnimate()
            .into(holder.contactImage)

        holder.contactName.text = contact.name
        holder.contactPhone.text = contact.phone_no

        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserFragment::class.java)
            intent.putExtra("name", contact.name)
            intent.putExtra("phone", contact.phone_no)
            intent.putExtra("imageId", contact.image_id)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.invoke(position, contact)
            true
        }

        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contact.phone_no}")
            context.startActivity(intent)
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<ContactModel>() {
        override fun areItemsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
            return oldItem == newItem
        }
    }
}
