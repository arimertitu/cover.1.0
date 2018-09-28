package com.example.project.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.hdodenhof.circleimageview.CircleImageView;


public class ShuffleFragment extends Fragment {


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
        return inflater.inflate(R.layout.fragment_shuffle, container, false);

    }


}
