package com.example.project.myapplication;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity {

    private MaterialEditText edtName,edtSurname;
    private Button btnSubmit;
    private ImageButton btnSelectDate;
    private TextView txtBirthday;
    private RadioButton genderMale,genderFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        Toolbar toolbar = findViewById(R.id.userInfo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Information");


        setupUIViews();


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



    }

    private void setupUIViews() {

        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtSurname = (MaterialEditText) findViewById(R.id.edtSurname);
        btnSelectDate = (ImageButton) findViewById(R.id.btnSelectDate);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        txtBirthday = (TextView) findViewById(R.id.txtbirthday);
        genderMale = (RadioButton) findViewById(R.id.gender_male);
        genderFemale = (RadioButton) findViewById(R.id.gender_female);



    }
}
