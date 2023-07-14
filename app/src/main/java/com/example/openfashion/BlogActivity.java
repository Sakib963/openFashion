package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
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

public class BlogActivity extends AppCompatActivity {
    AppCompatImageView hamburgerMenu;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;

    ConstraintLayout blog_post_one, blog_post_two, blog_post_three, blog_post_four;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        // Getting the layouts for blog posts
        blog_post_one = findViewById(R.id.blog_post_one_layout);
        blog_post_two = findViewById(R.id.blog_post_two_layout);
        blog_post_three = findViewById(R.id.blog_post_three_layout);
        blog_post_four = findViewById(R.id.blog_post_four_layout);

        // Setting up OnClickListener to send to BlogPost Activity
        blog_post_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BlogActivity.this, BlogPostActivity.class));
            }
        });
        blog_post_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BlogActivity.this, BlogPostActivity.class));
            }
        });
        blog_post_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BlogActivity.this, BlogPostActivity.class));
            }
        });
        blog_post_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BlogActivity.this, BlogPostActivity.class));
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
            startActivity(new Intent(BlogActivity.this, WelcomeActivity.class));
        }

        drawerLayout = findViewById(R.id.drawer_layout);


        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Displaying Underline to Contact Option In Menu
        MenuItem contactItem = navigationView.getMenu().findItem(R.id.optBlog);
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
                    startActivity(new Intent(BlogActivity.this, MainActivity.class));
                    finish();
                }
                if(id == R.id.optCart){
                    startActivity(new Intent(BlogActivity.this, CartActivity.class));
                    finish();
                }
                if(id == R.id.optOrders){
                    startActivity(new Intent(BlogActivity.this, OrderActivity.class));
                    finish();
                }
                if(id == R.id.optBlog){
                    startActivity(new Intent(BlogActivity.this, BlogActivity.class));
                    finish();
                }
                if(id == R.id.optContact){
                    startActivity(new Intent(BlogActivity.this, ContactActivity.class));
                    finish();
                }
                if(id == R.id.optLogout){
                    mAuth.signOut();
                    startActivity(new Intent(BlogActivity.this, LoginActivity.class));
                    finish();
                }


                return true;
            }
        });
    }
}