package com.example.customlistview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.customlistview.databinding.FragmentMessagesBinding

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var userArrayList: ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageId = intArrayOf(
            R.drawable.pic1, R.drawable.pic11, R.drawable.pic12, R.drawable.pic13, R.drawable.pic10,
            R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9
        )
        val name = arrayOf(
            "Myles the spidey", "Eyables", "Spakol", "Dayaper", "Hayden and Pogi", "Ayu the kups",
            "Jose the aswang", "Hayden the small", "Groc", "Kupalerski",
            "Grabe ba", "Angas naman", "Pogito"
        )
        val last_Message = arrayOf(
            "Goodluck baby, I love you!", "Pre ano sagot sa assignment", "You: Sent an image",
            "May issue daw si ano", "When tayo gawa capstone", "Saan may eabab?", "Hindi kita ma reach",
            "Emel tayo ya", "Ba't ba ang pogi mo", "Penge code", "Sige ganiyanan na",
            "May quiz na sa ITEL", "Punta tayo tbird"
        )
        val last_msg_time = arrayOf(
            "8:00 am", "9:41 am", "12:48 am", "4:04 pm", "10:55 am", "4:56 pm", "3:30 pm",
            "6:50 pm", "10:45 am", "7:00 pm", "2:40 pm", "6:00 pm", "11:56 pm",
        )
        val phone_no = arrayOf(
            "09123456789", "09123456789", "09123456789", "09123456789", "09123456789",
            "09123456789", "09123456789", "09123456789", "09123456789",
            "09123456789", "09123456789", "09123456789", "09123456789",
        )

        userArrayList = ArrayList()

        for (i in name.indices) {
            val user = User(name[i], last_Message[i], last_msg_time[i], phone_no[i], imageId[i])
            userArrayList.add(user)
        }

        binding.messagesRecyclerView.isClickable = true
        binding.messagesRecyclerView.adapter = MyAdapter(requireActivity(), userArrayList)
        // Click listener removed as it's no longer needed for messages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}