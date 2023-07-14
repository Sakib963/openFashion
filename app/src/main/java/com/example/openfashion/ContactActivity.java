package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContactActivity extends AppCompatActivity {
    AppCompatImageView hamburgerMenu;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;

    AppCompatTextView chat_with_us_description, message_description, text_us_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Getting Textview
        chat_with_us_description = findViewById(R.id.contact_us_description);
        message_description = findViewById(R.id.message_description);
        text_us_description = findViewById(R.id.private_message_description);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            chat_with_us_description.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            message_description.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            text_us_description.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        hamburgerMenu = findViewById(R.id.hamburger_button);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.user_name);
        ImageView userImage = headerView.findViewById(R.id.user_image);
        TextView userEmail = headerView.findViewById(R.id.user_email);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image);
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .apply(options)
                    .into(userImage);

            userName.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());
        }
        else{
            startActivity(new Intent(ContactActivity.this, WelcomeActivity.class));
        }

        drawerLayout = findViewById(R.id.drawer_layout);

        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Displaying Underline to Contact Option In Menu
        MenuItem contactItem = navigationView.getMenu().findItem(R.id.optContact);
        SpannableString spannableString = new SpannableString(contactItem.getTitle());
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        contactItem.setTitle(spannableString);

        // Displaying Menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);

                if(id == R.id.optHome){
                    startActivity(new Intent(ContactActivity.this, MainActivity.class));
                    finish();
                }
                if(id == R.id.optCart){
                    startActivity(new Intent(ContactActivity.this, CartActivity.class));
                    finish();
                }
                if(id == R.id.optOrders){
                    startActivity(new Intent(ContactActivity.this, OrderActivity.class));
                    finish();
                }
                if(id == R.id.optBlog){
                    startActivity(new Intent(ContactActivity.this, BlogActivity.class));
                    finish();
                }
                if(id == R.id.optLogout){
                    mAuth.signOut();
                    startActivity(new Intent(ContactActivity.this, LoginActivity.class));
                    finish();
                }


                return true;
            }
        });
    }
}