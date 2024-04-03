package com.example.halalcheck3;

// BusinessRegister.java
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halalcheck3.model.Business;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusinessRegister extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextPhone, editTextAddress;
    Button SignUp;
    TextView SignIn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        SignIn = findViewById(R.id.sign_in);
        SignUp = findViewById(R.id.sign_up);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextTextPostalAddress);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessRegister.this, BusinessLogin.class);
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

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(BusinessRegister.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(BusinessRegister.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(BusinessRegister.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phone.length() != 10) {
                    Toast.makeText(BusinessRegister.this, "Please enter 10 digits of your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(BusinessRegister.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Business object
                Business business = new Business(email, phone, address, password);

                // Save user information to the Realtime Database after authentication
                authenticateAndSaveToDatabase(business);
            }
        });
    }

    private void authenticateAndSaveToDatabase(Business business) {
        // Authenticate the user with email and password
        firebaseAuth.createUserWithEmailAndPassword(business.getEmail(), business.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Authentication successful, save user info to the Realtime Database
                            saveUserInfoToDatabase(business);
                        } else {
                            // If authentication fails, display an error message
                            Toast.makeText(BusinessRegister.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInfoToDatabase(Business business) {
        // Get a reference to the "businesses" node in the Realtime Database
        DatabaseReference businessesRef = FirebaseDatabase.getInstance().getReference("businesses");

        // Save business information to the Realtime Database
        businessesRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(business)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // If saving to the database is successful, display a success message
                            Toast.makeText(BusinessRegister.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Navigate to the login activity
                            Intent intent = new Intent(BusinessRegister.this, BusinessLogin.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If saving to the database fails, display an error message
                            Toast.makeText(BusinessRegister.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
