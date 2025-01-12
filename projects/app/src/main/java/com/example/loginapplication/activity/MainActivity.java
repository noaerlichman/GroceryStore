package com.example.loginapplication.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginapplication.R;
import com.example.loginapplication.models.User;
import com.example.loginapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public interface LoginCallback {
        void onLoginResult(boolean success, String username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(LoginCallback callback) {
        String email = ((EditText) findViewById(R.id.textMainEmailAddress)).getText().toString();
        String password = ((EditText) findViewById(R.id.textMainPassword)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the current authenticated user
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Encode email to make it safe for use as a Firebase key
                                String safeEmail = user.getEmail().replace(".", ",");

                                // Query the Firebase Realtime Database for user data
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(safeEmail)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    // Retrieve user data from Firebase
                                                    String firstName = snapshot.child("firstName").getValue(String.class);
                                                    String lastName = snapshot.child("lastName").getValue(String.class);
                                                    String username = firstName + " " + lastName;

                                                    // Call the callback with the username
                                                    callback.onLoginResult(true, username);
                                                } else {
                                                    // Handle case where user data doesn't exist
                                                    callback.onLoginResult(false, null);
                                                    Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                callback.onLoginResult(false, null);
                                                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                            callback.onLoginResult(false, null);
                        }
                    }
                });
    }



    public void register() {
        String email = ((EditText) findViewById(R.id.textFragRegEmailAddress)).getText().toString();
        String password = ((EditText) findViewById(R.id.textFragRegPassword)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful, show success message
                            Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                            // Now, call addData() to store additional user information
                            addData(); // Add user details (first name, last name, etc.) to the Firebase Realtime Database
                        } else {
                            // If registration fails, show error message
                            Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void addData() {
        // Get the input values
        String firstName = ((EditText) findViewById(R.id.textFragRegFirstName)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.textFragRegLastName)).getText().toString();
        String phone = ((EditText) findViewById(R.id.textFragRegPhone)).getText().toString();
        String email = ((EditText) findViewById(R.id.textFragRegEmailAddress)).getText().toString();

        // Check if any field is empty
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        // Get the current Firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get the UID of the current user
            String userId = currentUser.getUid();

            // Get a reference to the Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users").child(userId); // Use UID as the key

            // Create the user object
            User user = new User(firstName, lastName, phone, email);

            // Set the value in the Firebase Database
            myRef.setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        // On success
                        Toast.makeText(MainActivity.this, "User data added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // On failure
                        Toast.makeText(MainActivity.this, "Failed to add data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(MainActivity.this, "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }


}