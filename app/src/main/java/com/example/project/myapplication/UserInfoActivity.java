package com.example.project.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;
import java.util.HashMap;

public class UserInfoActivity extends AppCompatActivity {

    private MaterialEditText edtName,edtSurname;
    private Button btnSubmit;
    private ImageButton btnSelectDate;
    private TextView txtBirthday;
    private RadioGroup genderGroup;
    private String name,surname,birthday,gender,user_id;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        Toolbar toolbar = findViewById(R.id.userInfo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Information");

        setupUIViews();


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);








        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);
                int month = mcurrentTime.get(Calendar.MONTH);
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(UserInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
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


        btnSubmit.setOnClickListener(new View.OnClickListener() {
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

                name = edtName.getText().toString();
                surname = edtSurname.getText().toString();
                birthday = txtBirthday.getText().toString();

                if (checkValidate()) {

                    HashMap userMap = new HashMap();
                    userMap.put("name", name);
                    userMap.put("surname", surname);
                    userMap.put("birthday", birthday);
                    userMap.put("gender",gender);
                    userMap.put("image","");

                   databaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {

                           if (task.isSuccessful()){
                               Toast.makeText(UserInfoActivity.this, "Your information saved! ", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(UserInfoActivity.this, MainActivity.class));

                           }else {

                               Toast.makeText(UserInfoActivity.this, "Server is busy ", Toast.LENGTH_SHORT).show();
                           }

                       }
                   });


                }
            }
        });


    }



    private void setupUIViews() {

        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtSurname = (MaterialEditText) findViewById(R.id.edtSurname);
        btnSelectDate = (ImageButton) findViewById(R.id.btnSelectDate);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        txtBirthday = (TextView) findViewById(R.id.txtbirthday);
        genderGroup = (RadioGroup) findViewById(R.id.gender);




    }

    private Boolean checkValidate() {
        Boolean result = false;



        if (name.isEmpty() || surname.isEmpty() || birthday.isEmpty() || gender.isEmpty()) {
            Toast.makeText(UserInfoActivity.this, "Please enter all user information", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }
}
