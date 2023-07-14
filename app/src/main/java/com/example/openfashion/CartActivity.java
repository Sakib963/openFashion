package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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

public class CartActivity extends AppCompatActivity {
    AppCompatImageView hamburgerMenu;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;

    AppCompatButton checkout_button;


    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        hamburgerMenu = findViewById(R.id.hamburger_button);

        checkout_button = findViewById(R.id.checkout_button);


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
            startActivity(new Intent(CartActivity.this, WelcomeActivity.class));
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
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                    finish();
                }
                if(id == R.id.optOrders){
                    startActivity(new Intent(CartActivity.this, OrderActivity.class));
                    finish();
                }
                if(id == R.id.optBlog){
                    startActivity(new Intent(CartActivity.this, BlogActivity.class));
                    finish();
                }
                if(id == R.id.optContact){
                    startActivity(new Intent(CartActivity.this, ContactActivity.class));
                    finish();
                }
                if(id == R.id.optLogout){
                    mAuth.signOut();
                    startActivity(new Intent(CartActivity.this, LoginActivity.class));
                    finish();
                }


                return true;
            }
        });

        // Initialize the cart item list
        cartItemList = new ArrayList<>();


        // Initialize the cartAdapter with the cartItemList
        cartAdapter = new CartAdapter(cartItemList, CartActivity.this);

        // Displaying Cart Data
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the cartAdapter to your RecyclerView
        cartRecyclerView.setAdapter(cartAdapter);


        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(currentUser.getUid());
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = cartSnapshot.getValue(CartItem.class);
                    cartItemList.add(cartItem);
                }

                // Notify the adapter that the data has changed
                cartAdapter.notifyDataSetChanged();

                // Check if the cartItemList is empty
                if (cartItemList.isEmpty()) {
                    // Handle case when there are no cart items
                    // ...
                    TextView noItemFound = findViewById(R.id.no_item_found);
                    noItemFound.setVisibility(View.VISIBLE);
                    checkout_button.setVisibility(View.INVISIBLE);
                } else {
                    TextView noItemFound = findViewById(R.id.no_item_found);
                    noItemFound.setVisibility(View.GONE);
                    checkout_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the database operation
                // ...
                Toast.makeText(CartActivity.this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
            }
        });

        checkout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
                finish();
            }
        });
    }
}