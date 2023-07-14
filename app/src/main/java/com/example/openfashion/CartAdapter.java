package com.example.openfashion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;

    private DatabaseReference cartItemsRef;
    private FirebaseAuth firebaseAuth;

    public CartAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList != null ? cartItemList : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.serialNumberTextView.setText(String.valueOf(position + 1) + ".");
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.priceTextView.setText("$" + String.valueOf(cartItem.getPrice()));

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform "Delete" operation
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                int itemPosition = holder.getAdapterPosition(); // Get the dynamic position

                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                        .child("carts")
                        .child(currentUser.getUid())
                        .child(cartItem.getProductName());
                cartRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Delete operation successful
                                // Remove the item from the cartItemList
                                // Notify the adapter of the item removal
                                notifyItemRemoved(itemPosition);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Delete operation failed
                                // Handle the failure or display an error message
                                Toast.makeText(context, "Failed to delete item.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView serialNumberTextView;
        TextView productNameTextView;
        TextView priceTextView;
        ImageButton deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            serialNumberTextView = itemView.findViewById(R.id.serialNumberTextView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}


