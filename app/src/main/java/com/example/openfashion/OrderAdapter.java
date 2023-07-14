package com.example.openfashion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView serialNumberTextView;
        private LinearLayout productNamesContainer;
        private TextView priceTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            serialNumberTextView = itemView.findViewById(R.id.serialNumberTextView);
            productNamesContainer = itemView.findViewById(R.id.productNamesContainer);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }

        public void bind(Order order) {
            serialNumberTextView.setText(String.valueOf(getAdapterPosition() + 1 + ". "));
            priceTextView.setText("Total: "+String.valueOf(order.getTotalPrice()));

            productNamesContainer.removeAllViews(); // Clear existing product names

            // Add product names dynamically
            for (String productName : order.getProductNames()) {
                TextView productNameTextView = new TextView(itemView.getContext());
                productNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                productNameTextView.setText(productName);
                productNameTextView.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                productNameTextView.setTextAppearance(itemView.getContext(), android.R.style.TextAppearance_Medium);
                productNamesContainer.addView(productNameTextView);
            }
        }
    }
}
