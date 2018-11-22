package com.example.project.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;


public class ProfileSettingActivity extends AppCompatActivity {

    private static final String TAG="ProfileSettingActivity";
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
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private int secimgender;
    private Uri mImageUri;


    private static final int PICK_IMAGE_REQUEST = 123;
    private Uri filePath;
    private static int Gallery_Pick = 1;



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
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePhoto");






        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelect();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String mImageUri = preferences.getString("image", null);


        if (mImageUri != null) {
            try {
                Picasso.get().load(mImageUri).transform(new CircleTransform()).into(imgProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Picasso.get().load(R.drawable.ic_person_black_24dp).transform(new CircleTransform()).into(imgProfile);
        }

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
                            startActivity(new Intent(ProfileSettingActivity.this,ProfileFragment.class));

                        }else {

                            Toast.makeText(ProfileSettingActivity.this, "Server is busy ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }

        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data != null) {

                // This is the key line item, URI specifies the name of the data
                mImageUri = data.getData();

                final StorageReference filePath = storageReference.child(user_id + ".jpg");
                filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child("image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ProfileSettingActivity.this,"İşlem Başarılı",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                });


            }
            // Saves image URI as string to Default Shared Preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileSettingActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("image", String.valueOf(mImageUri));
            editor.commit();
            try {
                Picasso.get().load(mImageUri).transform(new CircleTransform()).into(imgProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void imageSelect() {
        permissionsCheck();
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void permissionsCheck() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
    }

}
