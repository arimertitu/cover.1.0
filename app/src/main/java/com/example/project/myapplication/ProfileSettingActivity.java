package com.example.project.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class ProfileSettingActivity extends AppCompatActivity {

    private MaterialEditText edtName,edtSurname,edtUsername;
    private TextView txtBirthday;
    private ImageView imgProfile;
    private FloatingActionButton btnChangePhoto;
    private MaterialEditText edtBio;
    private RadioGroup genderGroup;
    private RadioButton gender_male,gender_female;
    private AppCompatButton btnSave;
    private ImageButton btnChangeBirthday;
    private FirebaseAuth mAuth;
    private FirebaseStorage fStorage;
    private String user_id,gender;
    private StorageReference storageRef;
    private DatabaseReference databaseReference;
    private int secimgender;

    private static final int PICK_IMAGE_REQUEST = 123;
    private Uri filePath;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Picasso.get().load(filePath).fit().centerCrop().into(imgProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);


        btnChangePhoto = (FloatingActionButton) findViewById(R.id.btnChangePhoto);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        edtName = (MaterialEditText) findViewById(R.id.edtName);
        txtBirthday = (TextView) findViewById(R.id.txtbirthday);
        edtSurname = (MaterialEditText) findViewById(R.id.edtSurname);
        edtUsername= (MaterialEditText) findViewById(R.id.edtUsername);
        edtBio = (MaterialEditText) findViewById(R.id.edtBio);
        btnSave = (AppCompatButton) findViewById(R.id.btnSave);
        btnChangeBirthday = (ImageButton) findViewById(R.id.btnChangeDate);
        genderGroup = (RadioGroup) findViewById(R.id.gender);
        gender_female = (RadioButton) findViewById(R.id.gender_female);
        gender_male = (RadioButton) findViewById(R.id.gender_male);

        mAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String surname = dataSnapshot.child("surname").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                String currentGender = dataSnapshot.child("gender").getValue().toString();
                String currentBirthday = dataSnapshot.child("birthday").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();

                if (currentGender.equals("M")){
                    gender_male.setChecked(true);
                }else {
                    gender_female.setChecked(true);
                }


                txtBirthday.setText(currentBirthday);
                edtName.setText(name);
                edtSurname.setText(surname);
                edtUsername.setText(username);
                edtBio.setText(bio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnChangeBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);
                int month = mcurrentTime.get(Calendar.MONTH);
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(ProfileSettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtBirthday.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                    }
                },year,month,day);

                datePicker.setTitle("Select Date");
                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE,"Select",datePicker);
                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE,"Cancel",datePicker);

                datePicker.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secimgender = genderGroup.getCheckedRadioButtonId();

                switch (secimgender) {
                    case R.id.gender_male:
                        gender = "M";
                        break;
                    case R.id.gender_female:
                        gender = "F";
                        break;
                }

                String name = edtName.getText().toString();
                String surname = edtSurname.getText().toString();
                String birthday = txtBirthday.getText().toString();
                String userBio = edtBio.getText().toString();



                HashMap userMap = new HashMap();
                userMap.put("name", name);
                userMap.put("surname", surname);
                userMap.put("birthday", birthday);
                userMap.put("gender",gender);
                userMap.put("bio",userBio);


                databaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){
                            Toast.makeText(ProfileSettingActivity.this, "Your information saved! ", Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(ProfileSettingActivity.this, "Server is busy ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }

        });





    }
}
