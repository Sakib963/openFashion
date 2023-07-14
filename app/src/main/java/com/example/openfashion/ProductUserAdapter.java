package com.example.openfashion;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
public class ProductUserAdapter extends RecyclerView.Adapter<ProductUserAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private DatabaseReference cartItemsRef;
    private FirebaseAuth firebaseAuth;

    public ProductUserAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String price = "$ " + String.valueOf(product.getPrice());

        // Bind the product data to the views
        Glide.with(holder.itemView.getContext()).load(product.getImageURL()).into(holder.productImageView);

        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText(price);
        holder.productDescriptionTextView.setText(product.getDescription());
        String quantity = product.getQuantity() + " piece available";
        holder.productQuantityTextView.setText(quantity);

        cartItemsRef = FirebaseDatabase.getInstance().getReference().child("carts");
        firebaseAuth = FirebaseAuth.getInstance();

        // Set onClickListener for the "Add to Cart" button
        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform "Add to Cart" operation
                // ...
                String productName = product.getName();
                double price = product.getPrice();
                String userEmail = firebaseAuth.getCurrentUser().getEmail();
                String userUid = firebaseAuth.getCurrentUser().getUid();
                String itemId = cartItemsRef.push().getKey();

                // Create a new CartItem object
                CartItem cartItem = new CartItem(itemId, productName, price, userEmail, userUid);


                // Save the CartItem to the database under the "carts" node
                cartItemsRef.child(userUid).child(productName).setValue(cartItem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Cart item added successfully
                                Toast.makeText(holder.itemView.getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while adding to cart
                                Toast.makeText(holder.itemView.getContext(), "Failed to add product to cart", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView productDescriptionTextView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        AppCompatButton addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productDescriptionTextView = itemView.findViewById(R.id.productDescriptionTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}


