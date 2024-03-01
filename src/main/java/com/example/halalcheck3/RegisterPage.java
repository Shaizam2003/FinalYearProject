package com.example.halalcheck3;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halalcheck3.model.CustomerInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextPhone, editTextAddress;
    Button SignUp;
    TextView SignIn;
    TextView register_business;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        SignIn = findViewById(R.id.sign_in);
        SignUp = findViewById(R.id.sign_up);
        register_business = findViewById(R.id.business_register);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextTextPostalAddress);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPage.this, BusinessRegister.class);
                startActivity(intent);
                finish();
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, phone, address;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                phone = String.valueOf(editTextPhone.getText());
                address = String.valueOf(editTextAddress.getText());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
                    Toast.makeText(RegisterPage.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.length() != 10) {
                    Toast.makeText(RegisterPage.this, "Please enter 10 digits for your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create CustomerInfo object
                CustomerInfo customerInfo = new CustomerInfo(email, password, phone, address);

                // Save user information to the Realtime Database after authentication
                authenticateAndSaveToDatabase(customerInfo);
            }
        });
    }

    private void authenticateAndSaveToDatabase(CustomerInfo customerInfo) {
        // Authenticate the user with email and password
        firebaseAuth.createUserWithEmailAndPassword(customerInfo.getEmail(), customerInfo.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Authentication successful, save user info to the Realtime Database
                            saveUserInfoToDatabase(customerInfo);
                        } else {
                            // If authentication fails, display an error message
                            Toast.makeText(RegisterPage.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInfoToDatabase(CustomerInfo customerInfo) {
        // Get a reference to the "users" node in the Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Save user information to the Realtime Database
        usersRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(customerInfo)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // If saving to the database is successful, display a success message
                            Toast.makeText(RegisterPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Navigate to the main activity
                            Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If saving to the database fails, display an error message
                            Toast.makeText(RegisterPage.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
