package com.example.project.myapplication;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ShuffleFragment extends Fragment {

    private AppCompatButton btnShuffle;
    private TextView txtLocation;
    private String countryLocation;
    private AppCompatButton btnSelectCountry;




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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUIViews();

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        txtLocation.setText(countryLocation);


    }

    private void setupUIViews(){

        txtLocation = (TextView) getView().findViewById(R.id.txtLocation);
        btnShuffle = (AppCompatButton) getView().findViewById(R.id.btnShuffle);
        btnSelectCountry = (AppCompatButton) getView().findViewById(R.id.btnSelectLocation);


    }

}
