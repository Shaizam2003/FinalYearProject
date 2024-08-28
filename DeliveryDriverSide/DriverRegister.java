package com.example.halalcheck3.DeliveryDriverSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.DriverInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegister extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextPhone, editTextName;
    Button SignUp;
    TextView SignIn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        SignIn = findViewById(R.id.sign_in);
        SignUp = findViewById(R.id.sign_up);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextName = findViewById(R.id.editTextName);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverRegister.this, DriverLogin.class);
                startActivity(intent);
                finish();
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, phone, name;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                phone = String.valueOf(editTextPhone.getText());
                name = String.valueOf(editTextName.getText());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(name)) {
                    Toast.makeText(DriverRegister.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.length() != 10) {
                    Toast.makeText(DriverRegister.this, "Please enter 10 digits for your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create DriverInfo object with isAvailable set to true
                DriverInfo driverInfo = new DriverInfo(email, password, phone, name, true);

                // Save driver information to the Realtime Database after authentication
                authenticateAndSaveToDatabase(driverInfo);
            }
        });
    }

    private void authenticateAndSaveToDatabase(DriverInfo driverInfo) {
        // Authenticate the driver with email and password
        firebaseAuth.createUserWithEmailAndPassword(driverInfo.getEmail(), driverInfo.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Authentication successful, save driver info to the Realtime Database
                            saveDriverInfoToDatabase(driverInfo);
                        } else {
                            // If authentication fails, display an error message
                            Toast.makeText(DriverRegister.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveDriverInfoToDatabase(DriverInfo driverInfo) {
        // Get a reference to the "drivers" node in the Realtime Database
        DatabaseReference driversRef = FirebaseDatabase.getInstance().getReference("drivers");

        // Save driver information to the Realtime Database
        driversRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(driverInfo)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // If saving to the database is successful, display a success message
                            Toast.makeText(DriverRegister.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Navigate to the main activity
                            Intent intent = new Intent(DriverRegister.this, DriverLogin.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If saving to the database fails, display an error message
                            Toast.makeText(DriverRegister.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


