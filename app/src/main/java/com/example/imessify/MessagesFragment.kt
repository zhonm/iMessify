package com.example.imessify

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imessify.databinding.FragmentMessagesBinding

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapterArrayList: ArrayList<UserAdapter>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup back button listener to show logout dialog
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showLogoutConfirmationDialog()
                }
            }
        )

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

        userAdapterArrayList = ArrayList()

        for (i in name.indices) {
            val userAdapter = UserAdapter(name[i], last_Message[i], last_msg_time[i], phone_no[i], imageId[i])
            userAdapterArrayList.add(userAdapter)
        }

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.messagesRecyclerView.adapter = MessagesAdapter(requireActivity(), userAdapterArrayList)

        // Add search functionality
        binding.searchContacts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMessages(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_logout, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)

        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
        val btnLogout = dialogView.findViewById<TextView>(R.id.btnLogout)

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnLogout.setOnClickListener {
            logoutUser()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun logoutUser() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun filterMessages(query: String) {
        val filteredList = ArrayList<UserAdapter>()

        if (query.isEmpty()) {
            filteredList.addAll(userAdapterArrayList)
        } else {
            for (user in userAdapterArrayList) {
                if (user.name.lowercase().contains(query.lowercase()) ||
                    user.last_Message.lowercase().contains(query.lowercase())) {
                    filteredList.add(user)
                }
            }
        }

        (binding.messagesRecyclerView.adapter as MessagesAdapter).updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}