package com.example.openfashion;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class MainActivity extends AppCompatActivity {

    AppCompatImageView hamburgerMenu;
    DrawerLayout drawerLayout;

    private RecyclerView productRecyclerView;
    private DatabaseReference databaseReference;
    private ProductUserAdapter adapter;
    private List<Product> productList;

    FirebaseAuth mAuth;

    FloatingActionButton cart_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        }


        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // Displaying Underline to Home Option In Menu
        MenuItem contactItem = navigationView.getMenu().findItem(R.id.optHome);
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
//                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                }
                if(id == R.id.optCart){
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                    finish();
                }
                if(id == R.id.optOrders){
                    startActivity(new Intent(MainActivity.this, OrderActivity.class));
                    finish();
                }
                if(id == R.id.optBlog){
                    startActivity(new Intent(MainActivity.this, BlogActivity.class));
                    finish();
                }
                if(id == R.id.optContact){
                    startActivity(new Intent(MainActivity.this, ContactActivity.class));
                    finish();
                }
                if(id == R.id.optLogout){
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }


                return true;
            }
        });

        // Showing Products
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("products");


        productList = new ArrayList<>();
        adapter = new ProductUserAdapter(productList, MainActivity.this);
        productRecyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(MainActivity.this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
            }
        });


        cart_button = findViewById(R.id.cartButton);

        cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

    }
}