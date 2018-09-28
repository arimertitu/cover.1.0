package com.example.project.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignIn;
    private MaterialEditText edtPassword, edtEmail;
    private TextView linkSignUp, linkForgotPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        if (firebaseUser != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidate()) {
                    validate(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());
                }
            }
        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);

            }
        });

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(i);
            }
        });


    }

    private void setupUIViews() {

        btnSignIn = (Button) findViewById(R.id.btn_signIn);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtEmail = (MaterialEditText) findViewById(R.id.edtEmail);
        linkSignUp = (TextView) findViewById(R.id.link_signup);
        linkForgotPassword = (TextView) findViewById(R.id.link_forgotpassword);

    }


    private void validate(String user_email, String user_password) {



        firebaseAuth.signInWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    if (checkEmailVerification()) {


                        final String user_id = firebaseAuth.getCurrentUser().getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                        final DatabaseReference nameReference = databaseReference.child("name");

                        databaseReference.child("password").setValue(password);

                        nameReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, UserInfoActivity.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                } else {

                    Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    private Boolean checkValidate() {
        Boolean result = false;

         email = edtEmail.getText().toString();
         password = edtPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }


    private boolean checkEmailVerification() {
        firebaseUser = firebaseAuth.getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();

        if (emailFlag) {
            Toast.makeText(LoginActivity.this, "Login Succesfuly!", Toast.LENGTH_SHORT).show();
            return true;

        } else {
            Toast.makeText(LoginActivity.this, "Verify your email!", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            return false;
        }
    }
}