package com.example.project.myapplication;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    private static final String TAG="ProfileFragment";
    private Button btnLogout,btnEditProfile;
    private FloatingActionButton imgProfile;
    private TextView txtDisplayUsername, txtBio;
    private TextView txtDisplayName;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String user_id,name,surname,username,images,bio;
    private static int Gallery_Pick = 1;
    private Uri mImageUri;
    private GridView gridView;
    private ArrayList<String> img = new ArrayList<>();






    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragmentProfile = new ProfileFragment();
        return fragmentProfile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);




        imgProfile = (FloatingActionButton) view.findViewById(R.id.imgProfile);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnEditProfile = (Button) view.findViewById(R.id.btnEditProfile);
        txtDisplayName = (TextView) view.findViewById(R.id.txtName);
        txtDisplayUsername = (TextView) view.findViewById(R.id.txtUsername);
        txtBio = (TextView) view.findViewById(R.id.txtBio);
        gridView = (GridView) view.findViewById(R.id.gridView);



        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePhoto");









        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ProfileSettingActivity.class));


            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(),ProfileSettingActivity.class));
            }
        });



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mImageUri = preferences.getString("image", null);


        if (mImageUri != null) {

            Picasso.get().load(mImageUri).transform(new CircleTransform()).into(imgProfile);

        } else {
            imgProfile.setImageResource(R.drawable.ic_person_black_24dp);
        }








        return view;


    }





    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("name").getValue().toString();
                surname = dataSnapshot.child("surname").getValue().toString();
                username = dataSnapshot.child("username").getValue().toString();
                images = dataSnapshot.child("image").getValue().toString();
                bio = dataSnapshot.child("bio").getValue().toString();

                txtDisplayName.setText(name + " " + surname);
                txtBio.setText(bio);
                txtDisplayUsername.setText("@" + username);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    private void setupImageGrid(ArrayList<String> imgURLs){

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/2;
        gridView.setColumnWidth(imageWidth);

        ImageAdapter imageAdapter = new ImageAdapter(getActivity(),R.layout.gridview_image,"",imgURLs);
        gridView.setAdapter(imageAdapter);
        return;

    }




    @Override
    public void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            LogOutUser();
        }

    }



    private void LogOutUser() {

        startActivity(new Intent(getActivity(), LoginActivity.class));


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        return;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.profile_menu_logout) {
            firebaseAuth.signOut();

            LogOutUser();
        }

        if (item.getItemId() == R.id.profile_menu_setting) {
            startActivity(new Intent(getActivity(), ProfileSettingActivity.class));

        }


        return true;
    }


}