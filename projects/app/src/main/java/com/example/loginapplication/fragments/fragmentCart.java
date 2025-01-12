package com.example.loginapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.loginapplication.R;
import com.example.loginapplication.models.ShoppingItem;

import java.io.Serializable;
import java.util.List;

public class fragmentCart extends Fragment {
    private RecyclerView cartRecyclerView;
    private CartItemAdapter cartItemAdapter;
    private List<ShoppingItem> cartItemList;
    private String username;

    public fragmentCart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }
        // Get cart items from arguments
        if (getArguments() != null) {
            cartItemList = (List<ShoppingItem>) getArguments().getSerializable("cart_items");
        }

        // Initialize RecyclerView
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter with cart items
        cartItemAdapter = new CartItemAdapter(cartItemList);
        cartRecyclerView.setAdapter(cartItemAdapter);

        // Calculate total price
        double totalPrice = calculateTotalPrice(cartItemList);
        TextView totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        totalPriceTextView.setText("Total: $" + totalPrice);

        Button cartButton = view.findViewById(R.id.homeButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("cart_items", (Serializable) cartItemList);
                bundle.putSerializable("username", username);
                Navigation.findNavController(view).navigate(R.id.fragmentLogin, bundle);
            }
        });

        return view;
    }

    // Calculate total price of all cart items
    private double calculateTotalPrice(List<ShoppingItem> cartItemList) {
        double total = 0.0;
        for (ShoppingItem item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    // CartItem model class

    // RecyclerView Adapter for CartItem
    public static class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

        private List<ShoppingItem> cartItems;

        public CartItemAdapter(List<ShoppingItem> cartItems) {
            this.cartItems = cartItems;
        }

        @Override
        public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new CartItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CartItemViewHolder holder, int position) {
            ShoppingItem item = cartItems.get(position);
            holder.itemName.setText(item.getName());
            holder.itemPrice.setText("$" + item.getPrice());
            holder.itemQuantity.setText("Qty: " + item.getQuantity());

            // Remove item from cart
            holder.removeButton.setOnClickListener(v -> {
                cartItems.remove(position);
                notifyDataSetChanged(); // Update RecyclerView after removal
            });
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public static class CartItemViewHolder extends RecyclerView.ViewHolder {
            TextView itemName, itemPrice, itemQuantity;
            View removeButton;

            public CartItemViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.cartItemName);
                itemPrice = itemView.findViewById(R.id.cartItemPrice);
                itemQuantity = itemView.findViewById(R.id.cartItemQuantity);
                removeButton = itemView.findViewById(R.id.removeFromCartButton);
            }
        }
    }
}