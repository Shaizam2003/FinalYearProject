package com.example.halalcheck3.BusinessSide;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class BusinessLogin extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button SignIn;
    TextView SignUp;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_login);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        SignIn = findViewById(R.id.sign_in);
        SignUp = findViewById(R.id.sign_up);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessLogin.this, BusinessRegister.class);
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
                    Toast.makeText(BusinessLogin.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(BusinessLogin.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(BusinessLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(BusinessLogin.this, BusinessHomePage.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(BusinessLogin.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
            }
        });
    }
}


