package com.example.imessify

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.imessify.databinding.DialogNewMessageBinding

class NewMessageDialogFragment : DialogFragment() {

    private var _binding: DialogNewMessageBinding? = null
    private val binding get() = _binding!!
    
    private var onUsernameSelectedListener: ((String) -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogNewMessageBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up cancel button
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        
        // Set up start chat button
        binding.startChatButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            
            if (username.isEmpty()) {
                binding.usernameInput.error = "Please enter a username"
                return@setOnClickListener
            }
            
            // Call the listener with the selected username
            onUsernameSelectedListener?.invoke(username)
            dismiss()
        }
    }
    
    fun setOnUsernameSelectedListener(listener: (String) -> Unit) {
        onUsernameSelectedListener = listener
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}