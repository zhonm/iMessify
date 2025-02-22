package com.example.customlistview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.customlistview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var userArrayList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val imageId = intArrayOf(
            R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7,R.drawable.pic8,R.drawable.pic9
        )
        val name = arrayOf(
            "Myles the spidey","Ayu the kups","Jose the aswang","Hayden the small","Groc","Kupalerski",
            "Grabe ba","Angas naman","Pogito"
        )
        val last_Message = arrayOf(
            "Goodluck baby, I love you!", "Saan may eabab?","Hindi kita ma reach","Emel tayo ya","Ba't ba ang pogi mo","Penge code","Sige ganiyanan na","May quiz na sa ITEL",
            "Punta tayo tbird"
        )
        val last_msg_time = arrayOf(
            "8:00 am","4:56 pm","3:30 pm","6:50 pm","10:45 am","7:00 pm","2:40 pm","6:00 pm","11:56 pm",
        )
        val phone_no = arrayOf(
            "09123456789","09123456789","09123456789","09123456789","09123456789",
            "09123456789","09123456789","09123456789","09123456789",
        )

        userArrayList = ArrayList()

        for (i in name.indices) {
            val user = User(name[i], last_Message[i], last_msg_time[i], phone_no[i], imageId[i])
            userArrayList.add(user)
        }

        binding.listView.isClickable = true
        binding.listView.adapter = MyAdapter(this,userArrayList)
        binding.listView.setOnItemClickListener {parent,view,position,id->

            val name = name[position]
            val phone = phone_no[position]
            val imageId = imageId[position]

            val i = Intent(this,activity_user::class.java)
            i.putExtra("name",name)
            i.putExtra("phone",phone)
            i.putExtra("imageId",imageId)
            startActivity(i)
        }

    }
}