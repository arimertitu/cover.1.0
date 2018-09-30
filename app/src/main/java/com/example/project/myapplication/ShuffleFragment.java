package com.example.project.myapplication;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.OnCountryPickerListener;


public class ShuffleFragment extends Fragment implements OnCountryPickerListener {

    private AppCompatButton btnShuffle;
    private TextView txtLocation;
    private String countryLocation,user_id,premium;
    private AppCompatButton btnSelectCountry;
    private CountryPicker countryPicker;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;




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


        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        btnSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                user_id = firebaseAuth.getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot) {

                        premium = dataSnapshot.child("premium").getValue().toString();
                        if (premium.equals("0")) {
                            Toast.makeText(getActivity(),"Premium selected",Toast.LENGTH_LONG).show();
                        }else {
                            countryPicker.showDialog(getFragmentManager());
                        }

                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError) {

                    }
                });
            }
        });



    }



    private void setupUIViews(){


        txtLocation = (TextView) getView().findViewById(R.id.txtLocation);
        btnShuffle = (AppCompatButton) getView().findViewById(R.id.btnShuffle);
        btnSelectCountry = (AppCompatButton) getView().findViewById(R.id.btnSelectLocation);
        countryPicker = new CountryPicker.Builder().with(getActivity()).listener(this).build();


    }



    @Override
    public void onSelectCountry(Country country) {

        txtLocation.setText(country.getName());

    }
}


