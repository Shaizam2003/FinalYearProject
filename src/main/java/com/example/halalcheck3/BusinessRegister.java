package com.example.halalcheck3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class BusinessRegister extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextPhone, editTextAddress;
    Button SignUp;
    TextView SignIn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);

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
                }
                else if (phone.length() !=10) {
                    Toast.makeText(BusinessRegister.this, "Please enter 10 digits of your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(BusinessRegister.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }

                Spinner businessTypeSpinner = findViewById(R.id.businessTypeSpinner);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(BusinessRegister.this,
                        R.array.business_types,
                        android.R.layout.simple_spinner_item
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                businessTypeSpinner.setAdapter(adapter);

                businessTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Handle the selected item here
                        String selectedBusinessType = parentView.getItemAtPosition(position).toString();
                        Toast.makeText(BusinessRegister.this, "Selected Business Type: " + selectedBusinessType, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing here
                    }
                });

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(BusinessRegister.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(BusinessRegister.this, BusinessLogin.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(BusinessRegister.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }

        });
    }
}