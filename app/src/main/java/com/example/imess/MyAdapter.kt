package com.example.imess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(private val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(context).load(user.image_id).into(holder.imageView)
        holder.username.text = user.name
        holder.lastMessage.text = user.last_Message
        holder.lastMsgTime.text = user.last_Msg_time
    }

    override fun getItemCount(): Int = userList.size

    // Add this method to support filtered lists
    fun updateList(newList: ArrayList<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profile_pic)
        val username: TextView = itemView.findViewById(R.id.person_name)
        val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        val lastMsgTime: TextView = itemView.findViewById(R.id.msg_time)
    }
}