package com.example.imessify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imessify.R;
import com.example.imessify.models.ContactJava;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// Renamed to avoid conflict with Kotlin version
public class ContactsAdapterJava extends RecyclerView.Adapter<ContactsAdapterJava.ContactViewHolderJava> {
    
    private List<ContactJava> contactsList;
    private OnContactClickListenerJava contactClickListener;
    
    // Renamed to avoid conflict with Kotlin version
    public interface OnContactClickListenerJava {
        void onContactClick(ContactJava contact);
    }
    
    public ContactsAdapterJava(List<ContactJava> contactsList, OnContactClickListenerJava contactClickListener) {
        this.contactsList = contactsList;
        this.contactClickListener = contactClickListener;
    }
    
    @NonNull
    @Override
    public ContactViewHolderJava onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolderJava(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolderJava holder, int position) {
        ContactJava contact = contactsList.get(position);
        holder.bind(contact, contactClickListener);
    }
    
    @Override
    public int getItemCount() {
        return contactsList.size();
    }
    
    // Renamed to avoid conflict with Kotlin version
    public static class ContactViewHolderJava extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView nameText;
        private TextView phoneText;
        
        public ContactViewHolderJava(@NonNull View itemView) {
            super(itemView);
            // Using resource IDs that are more likely to exist in your project's layout files
            profileImage = itemView.findViewById(R.id.profile_pic);
            nameText = itemView.findViewById(R.id.contact_name);
            phoneText = itemView.findViewById(R.id.contact_phone);
        }
        
        public void bind(ContactJava contact, OnContactClickListenerJava listener) {
            nameText.setText(contact.getName());
            phoneText.setText(contact.getPhone());
            profileImage.setImageResource(contact.getProfilePicture());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContactClick(contact);
                }
            });
        }
    }
    
    public void updateContacts(List<ContactJava> newContacts) {
        this.contactsList = newContacts;
        notifyDataSetChanged();
    }
}
