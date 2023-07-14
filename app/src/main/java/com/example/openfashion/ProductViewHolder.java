package com.example.openfashion;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    public ImageView productImageView;
    public TextView productDescriptionTextView;
    public TextView productPriceTextView;
    public AppCompatButton addToCartButton;

    public ProductViewHolder(View itemView) {
        super(itemView);
        productImageView = itemView.findViewById(R.id.productImageView);
        productDescriptionTextView = itemView.findViewById(R.id.productDescriptionTextView);
        productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
        addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
    }
}

