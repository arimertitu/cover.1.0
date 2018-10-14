package com.example.project.myapplication;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class ProfileFragment extends Fragment {

    private static final String TAG="ProfileFragment";
    private FloatingActionButton btnChangePhoto;
    private Button btnLogout,btnEditProfile;
    private ImageView imgProfile;
    private TextView txtDisplayUsername, txtBio;
    private TextView txtDisplayName;
    private DatabaseReference databaseReference;
    private StorageReference storageReference,fileReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String user_id,name,surname,username,images,bio;
    private static int Gallery_Pick = 1;
    private Uri imageUri,resultUri;


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



        btnChangePhoto = (FloatingActionButton) view.findViewById(R.id.btnChangePhoto);
        imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnEditProfile = (Button) view.findViewById(R.id.btnEditProfile);
        txtDisplayName = (TextView) view.findViewById(R.id.txtName);
        txtDisplayUsername = (TextView) view.findViewById(R.id.txtUsername);
        txtBio = (TextView) view.findViewById(R.id.txtBio);


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_photo");

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(),ProfileSettingActivity.class));

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
                } else {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i, Gallery_Pick);
                }


            }
        });

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


        return view;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == Gallery_Pick && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(),ProfileFragment.this);


            Picasso.get()
                    .load(imageUri)
                    .into(imgProfile);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK){
                resultUri = result.getUri();
                uploadPhoto();
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);

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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    private void uploadPhoto() {

        if (imageUri != null) {

             fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();
                    ProfileImage imageUrl = new ProfileImage( taskSnapshot.getMetadata().getReference().getPath());


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });


        } else {
            Toast.makeText(getActivity(), "No file selected!", Toast.LENGTH_SHORT).show();
        }

    }

    private void setProfileImage(){
        Log.d(TAG,"setProfileImage: setting profile image.");
        String imgURL = "https://png2.kisspng.com/sh/eb808339bf6934991e40cead8695b39a/L0KzQYi4UsE3N2E6UJGAYUO4RLbpUvIzP2M5TZCCNUG7QYiBVsE2OWQ5TKQEOUS6Q4GCTwBvbz==/5a354eb2b27245.7518178615134429947309.png"
        Univer
    }
}