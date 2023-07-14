package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;

    AppCompatImageView hamburgerMenu;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        hamburgerMenu = findViewById(R.id.hamburger_button);



        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.user_name);
        ImageView userImage = headerView.findViewById(R.id.user_image);
        TextView userEmail = headerView.findViewById(R.id.user_email);

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
            startActivity(new Intent(OrderActivity.this, WelcomeActivity.class));
        }


        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // Displaying Underline to Home Option In Menu
        MenuItem contactItem = navigationView.getMenu().findItem(R.id.optCart);
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
                    startActivity(new Intent(OrderActivity.this, MainActivity.class));
                    finish();
                }
                if(id == R.id.optCart){
                    startActivity(new Intent(OrderActivity.this, CartActivity.class));
                    finish();
                }
                if(id == R.id.optOrders){
                    Toast.makeText(OrderActivity.this, "Already In This Activity.", Toast.LENGTH_SHORT).show();
                }
                if(id == R.id.optBlog){
                    startActivity(new Intent(OrderActivity.this, BlogActivity.class));
                    finish();
                }
                if(id == R.id.optContact){
                    startActivity(new Intent(OrderActivity.this, ContactActivity.class));
                    finish();
                }
                if(id == R.id.optLogout){
                    mAuth.signOut();
                    startActivity(new Intent(OrderActivity.this, LoginActivity.class));
                    finish();
                }


                return true;
            }
        });

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setHasFixedSize(true);


        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders").child(currentUser.getUid());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Order> orderList = new ArrayList<>();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String serialNumber = orderSnapshot.child("serialNumber").getValue(String.class);
                    List<String> productNames = new ArrayList<>();
                    for (DataSnapshot productSnapshot : orderSnapshot.child("productNames").getChildren()) {
                        String productName = productSnapshot.getValue(String.class);
                        productNames.add(productName);
                    }
                    double totalPrice = Double.parseDouble(orderSnapshot.child("totalPrice").getValue(String.class));
                    Order order = new Order(serialNumber, productNames, totalPrice);
                    orderList.add(order);
                }
                orderAdapter = new OrderAdapter(orderList);
                orderRecyclerView.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error gracefully
                Toast.makeText(OrderActivity.this, "Error retrieving orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}