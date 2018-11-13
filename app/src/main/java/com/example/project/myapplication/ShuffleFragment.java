package com.example.project.myapplication;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
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
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.OnCountryPickerListener;


import java.util.HashMap;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class ShuffleFragment extends Fragment implements OnCountryPickerListener {

    private AppCompatButton btnShuffle;
    private TextView txtLocation;
    private String countryLocation, user_id, premium;
    private AppCompatButton btnSelectCountry;
    private CountryPicker countryPicker;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, shuffleManReference, deleteReference, shuffleWomenReference;
    private Chronometer chronometer;
    private String isMatching = "0";
    private String  gender;
    private ProgressDialog shuffleProgressDialog;
    private long pauseOffset;
    private int count;
    private static SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMddHHmmss");


    public ShuffleFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ShuffleFragment newInstance() {
        ShuffleFragment fragment = new ShuffleFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        MainActivity activity = (MainActivity) getActivity();
        countryLocation = activity.country();


        return inflater.inflate(R.layout.fragment_shuffle, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUIViews();

        txtLocation.setText(countryLocation);


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        shuffleManReference = FirebaseDatabase.getInstance().getReference().child("Shuffle").child("Man");
        shuffleWomenReference = FirebaseDatabase.getInstance().getReference().child("Shuffle").child("Women");
        deleteReference = FirebaseDatabase.getInstance().getReference().child("Shuffle");

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Timestamp timestamp=new Timestamp(System.currentTimeMillis());

                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                shuffleProgressDialog = new ProgressDialog(getActivity());
                shuffleProgressDialog.setTitle("Matching");
                shuffleProgressDialog.setMessage("Please wait, while you are matching..");
                shuffleProgressDialog.setCancelable(false);
                shuffleProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (gender.equals("M")) {
                            deleteReference.child("Man").child(user_id).removeValue();
                        } else {
                            deleteReference.child("Women").child(user_id).removeValue();
                        }
                        dialog.dismiss();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        pauseOffset = 0;
                        chronometer.stop();
                    }
                });
                shuffleProgressDialog.show();


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        gender = dataSnapshot.child("gender").getValue().toString();


                        HashMap shuffleMap = new HashMap();
                        shuffleMap.put("date",   currentTime.format(timestamp));
                        shuffleMap.put("gender", gender);
                        shuffleMap.put("isMatch", isMatching);
                        shuffleMap.put("location", txtLocation.getText().toString());

                        if (gender.equals("M")) {
                            shuffleManReference.child(user_id).updateChildren(shuffleMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        shuffleWomenReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                 count = (int) dataSnapshot.getChildrenCount();

                                                if (count == 0){
                                                   deleteReference.child("Man").child(user_id).removeValue();
                                                    shuffleProgressDialog.dismiss();
                                                    chronometer.setBase(SystemClock.elapsedRealtime());
                                                    pauseOffset = 0;
                                                    chronometer.stop();
                                                    Toast.makeText(getActivity(),"Please try again!",Toast.LENGTH_LONG).show();
                                                }else{
                                                    //şu şartlara göre eşleştirme işlemi burada yapılacak..
                                                    Toast.makeText(getActivity(),"Eşleştirme devam ediyor",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "Tekrar deneyin..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            shuffleWomenReference.child(user_id).updateChildren(shuffleMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        shuffleManReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int count = (int) dataSnapshot.getChildrenCount();
                                                if (count == 0){
//                                                    deleteReference.child("Women").child(user_id).removeValue();
                                                    shuffleProgressDialog.dismiss();
                                                    chronometer.setBase(SystemClock.elapsedRealtime());
                                                    pauseOffset = 0;
                                                    chronometer.stop();
                                                    Toast.makeText(getActivity(),"Please try again!",Toast.LENGTH_LONG).show();
                                                }else{
                                                    //şu şartlara göre eşleştirme işlemi burada yapılacak..
                                                    Toast.makeText(getActivity(),"eşleştirme devam ediyor",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "Tekrar deneyin..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btnSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        premium = dataSnapshot.child("premium").getValue().toString();
                        if (premium.equals("1")) {
                            countryPicker.showDialog(getFragmentManager());

                        } else {
                            Toast.makeText(getActivity(), "Premium selected", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    private void setupUIViews() {


        txtLocation = (TextView) getView().findViewById(R.id.txtLocation);
        btnShuffle = (AppCompatButton) getView().findViewById(R.id.btnShuffle);
        btnSelectCountry = (AppCompatButton) getView().findViewById(R.id.btnSelectLocation);
        countryPicker = new CountryPicker.Builder().with(getActivity()).listener(this).build();
        chronometer = (Chronometer) getView().findViewById(R.id.chronometerExample);


    }


    @Override
    public void onSelectCountry(Country country) {


        txtLocation.setText(country.getName());


    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gender.equals("M")) {
            deleteReference.child("Man").child(user_id).removeValue();
        } else {
            deleteReference.child("Women").child(user_id).removeValue();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (gender.equals("M")) {
            deleteReference.child("Man").child(user_id).removeValue();
        } else {
            deleteReference.child("Women").child(user_id).removeValue();
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gender.equals("M")) {
            deleteReference.child("Man").child(user_id).removeValue();
        } else {
            deleteReference.child("Women").child(user_id).removeValue();
        }

    }

}


