package com.example.loginapplication.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapplication.R;
import com.example.loginapplication.models.ShoppingItem;
import com.example.loginapplication.services.ShoppingItemAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FragmentLogin extends Fragment {

    private RecyclerView shoppingRecyclerView;
    private List<ShoppingItem> shoppingItemsList = new ArrayList<>();
    private List<ShoppingItem> chosenItemsList = new ArrayList<>();
    private String username;

    public FragmentLogin() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Retrieve the username from the arguments
        if (getArguments() != null) {
            username = getArguments().getString("username");
            TextView usernameTextView = view.findViewById(R.id.greetingTextView);
            usernameTextView.setText("Hello " + username + "!");
        }

        // Get cart items from arguments (if available)
        if (getArguments() != null && getArguments().containsKey("cart_items")) {
            chosenItemsList = (List<ShoppingItem>) getArguments().getSerializable("cart_items");
        }

        // Initialize RecyclerView
        shoppingRecyclerView = view.findViewById(R.id.shoppingRecyclerView);
        shoppingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the Adapter
        ShoppingItemAdapter shoppingItemAdapter = new ShoppingItemAdapter(shoppingItemsList, chosenItemsList);
        shoppingRecyclerView.setAdapter(shoppingItemAdapter);  // Set the adapter first

        // Add static items to the list
        addStaticItems();

        // Notify the adapter to update the RecyclerView
        shoppingItemAdapter.notifyDataSetChanged();

        // Cart button click listener
        Button cartButton = view.findViewById(R.id.cartButton);
        cartButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("cart_items", (Serializable) chosenItemsList);
            bundle.putSerializable("username", username);
            Navigation.findNavController(view).navigate(R.id.fragment_cart, bundle);
        });

        return view;
    }

    private void addStaticItems() {
        shoppingItemsList.add(new ShoppingItem("Apples", 1.99, 1));
        shoppingItemsList.add(new ShoppingItem("Bananas", 0.59, 1));
        shoppingItemsList.add(new ShoppingItem("Milk", 2.99, 1));
        shoppingItemsList.add(new ShoppingItem("Bread", 2.49, 1));
        shoppingItemsList.add(new ShoppingItem("Eggs", 1.99, 1));
        shoppingItemsList.add(new ShoppingItem("Chicken", 7.99, 1));
        shoppingItemsList.add(new ShoppingItem("Rice", 1.59, 1));
        shoppingItemsList.add(new ShoppingItem("Beans", 0.99, 1));
        shoppingItemsList.add(new ShoppingItem("Tomatoes", 1.29, 1));
        shoppingItemsList.add(new ShoppingItem("Onions", 0.79, 1));
    }
}
