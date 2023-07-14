package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    private TextView totalProductsTextView;
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;

    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

    // 1. Retrieve the data from the Firebase Realtime Database under the "products" node.
    // 2. Create a new activity called AllProductActivity and design its layout XML file with a RecyclerView to display the product cards.
    // 3. Create a new model class Product to represent the product data.
    // 4. Create a new adapter class ProductAdapter that extends RecyclerView.Adapter and binds the product data to the card layout.
    // 5. In the AllProductActivity, initialize the RecyclerView and set the adapter to display the product cards.
    // 6. Retrieve the product data from the Firebase Realtime Database and convert it into a list of Product objects.
    // 7. Pass the list of products to the ProductAdapter and set it as the adapter for the RecyclerView.

        totalProductsTextView = findViewById(R.id.totalProductsTextView);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the Firebase Realtime Database reference
        productsRef = FirebaseDatabase.getInstance().getReference("products");

        // Retrieve the product data and set it in the adapter
        retrieveProductData();
    }

    private void retrieveProductData() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    // Extract product data and create Product objects
                    Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                // Update the total products count
                totalProductsTextView.setText("Total Products: " + productList.size());

                // Set the product list in the adapter and attach it to the RecyclerView
                productAdapter = new ProductAdapter(productList, AllProductsActivity.this);
                productRecyclerView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if retrieval is unsuccessful
                Toast.makeText(AllProductsActivity.this, "Failed to retrieve products: " +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}