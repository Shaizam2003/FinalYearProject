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

import com.example.halalcheck3.BusinessSide.BusinessLogin;
import com.example.halalcheck3.DeliveryDriverSide.DriverLogin;
import com.example.halalcheck3.model.CustomerInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button SignIn;
    TextView SignUp;
    TextView Login;
    TextView DriverLogin;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        SignIn = findViewById(R.id.sign_in);
        SignUp = findViewById(R.id.sign_up);
        Login = findViewById(R.id.business_login);
        DriverLogin = findViewById(R.id.driver_login);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterPage.class);
                startActivity(intent);
                finish();

            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BusinessLogin.class);
                startActivity(intent);
                finish();

            }
        });

        DriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DriverLogin.class);
                startActivity(intent);
                finish();

            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Retrieve the current user ID
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String userId = currentUser.getUid();

                                    // Show a success message
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                    // Navigate to CustomerHomeActivity, passing the user ID
                                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                                    intent.putExtra("USER_ID", userId); // Pass user ID to the Home Activity
                                    startActivity(intent);
                                    finish(); // Close the MainActivity
                                } else {
                                    // Show an error message
                                    Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private void retrieveUserInfoFromDatabase(String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String userId = email.replace(".", "_");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CustomerInfo customerInfo = dataSnapshot.getValue(CustomerInfo.class);

                    // Do something with the retrieved user information
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}

