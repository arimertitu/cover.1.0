package com.example.project.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private TextView txtTitle;
    private CircleImageView imgProfile;
    private TextView txtDisplayUsername,txtBio;
    private MaterialEditText edtBio;
    private TextView txtDisplayName;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String user_id;


    public ProfileFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragmentProfile = new ProfileFragment();
        return fragmentProfile;
    }
    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState){
        imgProfile = (CircleImageView) getView().findViewById(R.id.imgProfile);
        txtDisplayName = (TextView) getView().findViewById(R.id.txtName);
        txtDisplayUsername = (TextView) getView().findViewById(R.id.txtUsername);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String surname = dataSnapshot.child("surname").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();



                txtDisplayName.setText(name+" "+surname);

                txtDisplayUsername.setText(username);


            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }



}