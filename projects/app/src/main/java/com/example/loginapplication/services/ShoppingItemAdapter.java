package com.example.loginapplication.services;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginapplication.R;
import com.example.loginapplication.models.ShoppingItem;
import java.util.List;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder> {
    private List<ShoppingItem> shoppingItems;
    private List<ShoppingItem> chosenItems;

    public ShoppingItemAdapter(List<ShoppingItem> shoppingItems, List<ShoppingItem> chosenItems) {
        this.shoppingItems = shoppingItems;
        this.chosenItems = chosenItems;
    }

    @Override
    public ShoppingItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new ShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingItemViewHolder holder, int position) {
        ShoppingItem item = shoppingItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(String.format("$%.2f", item.getPrice()));

        // Find the corresponding item in the chosenItems list
        ShoppingItem chosenItem = findItemInChosenList(item);

        // Update button text and quantity based on selection
        if (chosenItem != null) {
            holder.addToCartButton.setText("Remove");
            holder.quantityText.setText(String.valueOf(chosenItem.getQuantity()));
        } else {
            holder.addToCartButton.setText("Add to Cart");
            holder.quantityText.setText("1"); // Initialize quantity to 1 if not in chosen list
        }

        // Handle Add/Remove button click
        holder.addToCartButton.setOnClickListener(v -> {
            if (chosenItem != null) {
                chosenItems.remove(chosenItem);
            } else {
                chosenItems.add(new ShoppingItem(item.getName(), item.getPrice(),
                        Integer.parseInt(holder.quantityText.getText().toString())));            }
            notifyDataSetChanged();
        });

        // Handle + button click for quantity (updates quantity text only)
        holder.btnPlus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.quantityText.getText().toString());
            holder.quantityText.setText(String.valueOf(currentQuantity + 1));
        });

        // Handle - button click for quantity (updates quantity text only)
        holder.btnMinus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.quantityText.getText().toString());
            if (currentQuantity > 1) {
                holder.quantityText.setText(String.valueOf(currentQuantity - 1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public static class ShoppingItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, quantityText;
        Button addToCartButton, btnPlus, btnMinus;

        public ShoppingItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }

    private ShoppingItem findItemInChosenList(ShoppingItem item) {
        for (ShoppingItem chosenItem : chosenItems) {
            if (chosenItem.getName().equals(item.getName())) {
                return chosenItem;
            }
        }
        return null;
    }
}