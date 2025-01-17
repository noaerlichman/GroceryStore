package com.example.loginapplication.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
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
        String firstNameInput = ((EditText)findViewById(R.id.textFragRegFirstName)).getText().toString();
        String lastNameInput = ((EditText)findViewById(R.id.textFragRegLastName)).getText().toString();
        String phoneInput = ((EditText)findViewById(R.id.textFragRegPhone)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful, show success message
                            addData(firstNameInput, lastNameInput, phoneInput, email);
                            Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                            // Now, call addData() to store additional user information
                             // Add user details (first name, last name, etc.) to the Firebase Realtime Database
                        } else {
                            // If registration fails, show error message
                            Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void addData(String firstname, String lastname, String phone, String email) {

        if (firstname == null || lastname == null || phone == null || email == null) {
            Toast.makeText(MainActivity.this, "Error: Missing UI elements", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if any field is empty and show specific feedback
        if (firstname.isEmpty()) {
            return;
        }
        if (lastname.isEmpty()) {
            return;
        }
        if (phone.isEmpty() || !phone.matches("[0-9]+")) { // Simple phone validation
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return;
        }

        // Get the current Firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get the user's email and sanitize it
            String emailKey = currentUser.getEmail();
            if (emailKey != null) {
                emailKey = emailKey.replace(".", ","); // Replace dots with commas
            } else {
                Toast.makeText(MainActivity.this, "Email is null for the current user", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get a reference to the Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users").child(emailKey); // Use sanitized email as the key

            // Create the user object
            User user = new User(firstname, lastname, phone, email);

            // Set the value in the Firebase Database
            myRef.setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "User data added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to add data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(MainActivity.this, "No authenticated user found", Toast.LENGTH_SHORT).show();
        }

    }



}