package com.example.project.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends AppCompatActivity {

    private Button btnSignUp;
    private MaterialEditText edtPassword, edtUsername, edtEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String username, email, password,premium,user_email,user_password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUIViews(); //xml dosya idleri bu method da

        Toolbar toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    user_email = edtEmail.getText().toString().trim();
                    user_password = edtPassword.getText().toString().trim();
                    premium ="0";


                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Users.username = username;
                                Users.email = email;
                                Users.premium = premium;

                                String user_id = firebaseAuth.getCurrentUser().getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                                databaseReference.child("username").setValue(username);
                                databaseReference.child("email").setValue(email);
                                databaseReference.child("premium").setValue(premium);
                                databaseReference.child("password").setValue(password)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    sendEmailVerification();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Server is busy!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.register_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }


    private void setupUIViews() {
        btnSignUp = (Button) findViewById(R.id.btn_signUp);
        edtEmail = (MaterialEditText) findViewById(R.id.edtEmail);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtUsername = (MaterialEditText) findViewById(R.id.edtUsername);

    }

    private Boolean validate() {
        Boolean result = false;

        username = edtUsername.getText().toString();
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {

            Toast.makeText(RegisterActivity.this, R.string.enter_details, Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;

    }

    private void sendEmailVerification() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }


    }


}
