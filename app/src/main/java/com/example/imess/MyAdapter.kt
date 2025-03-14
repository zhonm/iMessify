package com.example.imess

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MyAdapter(private val context: Activity, private val arrayList: ArrayList<User>)
    : ArrayAdapter<User>(context, R.layout.list_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val rowView: View

        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            holder = ViewHolder(
                rowView.findViewById(R.id.profile_pic),
                rowView.findViewById(R.id.person_name),
                rowView.findViewById(R.id.last_message),
                rowView.findViewById(R.id.msg_time)
            )
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = rowView.tag as ViewHolder
        }

        val user = arrayList[position]
        Glide.with(holder.imageView).load(user.image_id).into(holder.imageView)
        holder.username.text = user.name
        holder.lastMessage.text = user.last_Message
        holder.lastMsgTime.text = user.last_Msg_time

        return rowView
    }

    private data class ViewHolder(
        val imageView: ImageView,
        val username: TextView,
        val lastMessage: TextView,
        val lastMsgTime: TextView
    )
}