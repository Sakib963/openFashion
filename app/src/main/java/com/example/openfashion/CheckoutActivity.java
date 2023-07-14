package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    AppCompatImageView hamburgerMenu;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;

    AppCompatButton checkout_button;

    AppCompatTextView sub_total_text, total_text;

    AppCompatEditText name_edit_text, address_edit_text, phone_edit_text;

    double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        checkout_button = findViewById(R.id.checkout_button);
        name_edit_text = findViewById(R.id.name_edit_text);
        address_edit_text = findViewById(R.id.address_edit_text);
        phone_edit_text = findViewById(R.id.phone_edit_text);

        hamburgerMenu = findViewById(R.id.hamburger_button);

        sub_total_text = findViewById(R.id.sub_total_text);
        total_text = findViewById(R.id.total_text);

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
            startActivity(new Intent(CheckoutActivity.this, WelcomeActivity.class));
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
                    startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
                    finish();
                }
                if(id == R.id.optOrders){
                    startActivity(new Intent(CheckoutActivity.this, OrderActivity.class));
                    finish();
                }
                if(id == R.id.optBlog){
                    startActivity(new Intent(CheckoutActivity.this, BlogActivity.class));
                    finish();
                }
                if(id == R.id.optContact){
                    startActivity(new Intent(CheckoutActivity.this, ContactActivity.class));
                    finish();
                }
                if(id == R.id.optLogout){
                    mAuth.signOut();
                    startActivity(new Intent(CheckoutActivity.this, LoginActivity.class));
                    finish();
                }


                return true;
            }
        });

        // Initialize the cart item list
        cartItemList = new ArrayList<>();


        // Initialize the cartAdapter with the cartItemList
        cartAdapter = new CartAdapter(cartItemList, CheckoutActivity.this);

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
                totalPrice = 0;
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = cartSnapshot.getValue(CartItem.class);
                    cartItemList.add(cartItem);

                    double itemPrice = cartItem.getPrice();
                    totalPrice += itemPrice;
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
                    sub_total_text.setText(String.valueOf(totalPrice));
                    total_text.setText(String.valueOf(totalPrice + 50));

                    TextView noItemFound = findViewById(R.id.no_item_found);
                    noItemFound.setVisibility(View.GONE);
                    checkout_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the database operation
                // ...
                Toast.makeText(CheckoutActivity.this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
            }
        });

        checkout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_edit_text.getText().toString().trim();
                String address = address_edit_text.getText().toString().trim();
                String phoneNumber = phone_edit_text.getText().toString().trim();

                // Check if any of the shipping details are empty
                if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Please enter all shipping details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate a unique key for the order
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("orders").child(currentUser.getUid()).push();

                // Save the order information to the database
                orderRef.child("userEmail").setValue(currentUser.getEmail());
                orderRef.child("totalPrice").setValue(String.valueOf(totalPrice + 50));
                orderRef.child("shippingDetails").child("name").setValue(name);
                orderRef.child("shippingDetails").child("address").setValue(address);
                orderRef.child("shippingDetails").child("phoneNumber").setValue(phoneNumber);

                DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");

                // Save each product name in the cart to the database
                for (CartItem cartItem : cartItemList) {
                    String productName = cartItem.getProductName();
                    // Query the database to find the product by its name
                    Query query = productsRef.orderByChild("name").equalTo(productName);
                    // Decrease the quantity of the found product by 1
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                String productId = productSnapshot.getKey();
                                Product product = productSnapshot.getValue(Product.class);

                                // Update the quantity in the database
                                if (product != null) {
                                    int newQuantity = product.getQuantity() - 1;
                                    product.setQuantity(newQuantity);
                                    productsRef.child(productId).setValue(product);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any database error
                        }
                    });
                    orderRef.child("productNames").push().setValue(productName);
                }
                // Delete the cart products from the database
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(currentUser.getUid());
                cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                            CartItem cartItem = cartSnapshot.getValue(CartItem.class);
                            String cartItemId = cartSnapshot.getKey();

                            // Remove the cart item from the database
                            cartRef.child(cartItemId).removeValue();
                        }

                        // Clear the cartItemList after checkout
                        cartItemList.clear();
                        cartAdapter.notifyDataSetChanged();

                        // Show a success message to the user
                        Toast.makeText(CheckoutActivity.this, "Checkout successful", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors that occur during the database operation
                        // ...
                        Toast.makeText(CheckoutActivity.this, "Failed to delete cart items", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}