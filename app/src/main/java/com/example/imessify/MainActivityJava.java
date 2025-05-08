package com.example.imessify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivityJava extends AppCompatActivity implements DrawerInterface {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Find the drawer layout by ID - handle this safely in case the ID doesn't exist
            drawerLayout = findViewById(R.id.container); // Using container as a fallback ID
        } catch (Exception e) {
            // If the drawer_layout ID doesn't exist, log the error and continue
            drawerLayout = null;
        }

        // Get a reference to the profile image
        CircleImageView profileImage = findViewById(R.id.profile_pic);
        
        if (profileImage != null) {
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Use the Kotlin MainActivity class for navigation
                    Intent intent = new Intent(MainActivityJava.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    // Implement methods required by the DrawerInterface
    @Override
    public void openDrawer() {
        if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void closeDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }
}
