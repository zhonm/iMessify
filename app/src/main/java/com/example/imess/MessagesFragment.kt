package com.example.imess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imess.databinding.FragmentMessagesBinding
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager

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
            R.drawable.pic1, R.drawable.pic11, R.drawable.pic13, R.drawable.pic12, R.drawable.pic10,
            R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9
        )
        val name = arrayOf(
            "Zhon the designerist", "Hayden the fisher", "Ayu the kups", "Spakol", "Kupalerski", "Chimken",
            "Jose the aswang", "Hayden the small", "Groc", "Mang kepweng",
            "Grabe ba", "Angas naman", "Pogito"
        )
        val last_Message = arrayOf(
            "Tara ahon wala naman class", "Laki ba ng isda ko", "Sipat sipat lang aq here",
            "You: Sent an image", "When tayo gawa capstone", "Saan may eabab?", "Hindi kita ma reach",
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

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.messagesRecyclerView.adapter = MyAdapter(requireActivity(), userArrayList)

        // Add search functionality
        binding.searchContacts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMessages(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterMessages(query: String) {
        val filteredList = ArrayList<User>()

        if (query.isEmpty()) {
            filteredList.addAll(userArrayList)
        } else {
            for (user in userArrayList) {
                if (user.name.lowercase().contains(query.lowercase()) ||
                    user.last_Message.lowercase().contains(query.lowercase())) {
                    filteredList.add(user)
                }
            }
        }

        (binding.messagesRecyclerView.adapter as MyAdapter).updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}