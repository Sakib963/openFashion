package com.example.openfashion;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        String price = "$ " + String.valueOf(product.getPrice());
        String quantity = String.valueOf(product.getQuantity()) + "Piece Available";


        // Load the image using Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImageURL())
                .into(holder.productImageView);


        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText(price);
        holder.productDescriptionTextView.setText(product.getDescription());
        holder.productQuantityTextView.setText(quantity);

        // Set OnClickListener for the card
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProductAdminActivity and pass the product ID as an extra
                Intent intent = new Intent(context, ProductAdminActivity.class);
                intent.putExtra("productId", product.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productDescriptionTextView;
        // ... declare other views for the card layout
        TextView productQuantityTextView;
        ImageView productImageView;


        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productDescriptionTextView = itemView.findViewById(R.id.productDescriptionTextView);
            // ... initialize other views as needed
            productQuantityTextView  = itemView.findViewById(R.id.productQuantityTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
        }
    }
}

