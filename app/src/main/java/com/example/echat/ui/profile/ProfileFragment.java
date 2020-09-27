package com.example.echat.ui.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.echat.Model.User;
import com.example.echat.R;
import com.example.echat.Util.Helper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.security.PrivateKey;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int IMAGE_REQUEST = 1;
    private static int ENABLE_ONCLICK = 0;
    private String imageUriStr = "";

    private CircleImageView profileImage, saveImage, editImage;
    private TextView profileEmail, profileUserName, profileNumber;
    private EditText editProfileUsername, editProfileNumber;

    private ProfileViewModel profileViewModel;
    private Helper helper;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

    private Uri imageUri;
    private StorageTask storageTask;

    public ProfileFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper = new Helper(getContext());
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child(currentUser.getUid());

        profileImage = root.findViewById(R.id.profile_image);
        //code to enable onclick listener for profile image only when user want to edit profile image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ENABLE_ONCLICK == 1){
                    openSaveImage();
                }else if(ENABLE_ONCLICK == 0){
                    //TODO DONE: toast message for user to know that he has to click edit button to edit image
                    helper.toastMessage("Please click edit button to change profile");
                }
            }
        });
        saveImage = root.findViewById(R.id.save_profile_info);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ENABLE_ONCLICK = 0;
                saveprofileInfo();
                loadProfileInfo();
                changeVisibiltySave();
            }
        });
        editImage = root.findViewById(R.id.edit_profile_info);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ENABLE_ONCLICK = 1;
                changeVisibiltyEdit();
            }
        });

        profileUserName = root.findViewById(R.id.profile_username);
        profileEmail = root.findViewById(R.id.profile_email);
        profileNumber = root.findViewById(R.id.profile_number);

        editProfileNumber = root.findViewById(R.id.profile_number_edit);
        editProfileUsername = root.findViewById(R.id.profile_username_edit);

        //call function loadProfileInfo to load profile info on fragment loading
        loadProfileInfo();

        return root;
    }

    private void changeVisibiltyEdit() {
        helper.progressDialogStart("Please Wait", "Checking internet connection");
        databaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(snapshot.exists()){
                    assert user != null;
                    editProfileUsername.setText(user.getUsername());
                    editProfileNumber.setText(user.getNumber());

                    profileNumber.setVisibility(View.GONE);
                    profileUserName.setVisibility(View.GONE);
                    editImage.setVisibility(View.GONE);

                    editProfileUsername.setVisibility(View.VISIBLE);
                    editProfileNumber.setVisibility(View.VISIBLE);
                    saveImage.setVisibility(View.VISIBLE);

                    helper.progressDialogEnd();
                }else{
                    helper.toastMessage("snapshot does not exist");
                    helper.progressDialogEnd();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void changeVisibiltySave() {
        profileNumber.setVisibility(View.VISIBLE);
        profileUserName.setVisibility(View.VISIBLE);
        editImage.setVisibility(View.VISIBLE);

        editProfileUsername.setVisibility(View.GONE);
        editProfileNumber.setVisibility(View.GONE);
        saveImage.setVisibility(View.GONE);
    }

    //function to load newly updated profile info
    private void loadProfileInfo(){
        helper.progressDialogStart("Please Wait", "Loading profile info");
        databaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(snapshot.exists()){
                    assert user != null;
                    profileUserName.setText(user.getUsername());
                    profileEmail.setText(user.getEmail());
                    profileNumber.setText(user.getNumber());

                    if(!user.getProfileImageUrl().equals("default")){
                        Glide.with(getContext()).load(user.getProfileImageUrl()).into(profileImage);
                    }
                    helper.progressDialogEnd();
                }else{
                    helper.toastMessage("snapshot doesn't exist");
                    helper.logMessage(ProfileFragment.class, "snapshot doesn't exist");
                    helper.progressDialogEnd();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //function to save profile info online to firebase
    private void saveprofileInfo(){
        helper.progressDialogStart("Please Wait", "Saving profile info");
        HashMap<String, String> hashMap  = new HashMap<>();
        String profileImageUrl = imageUriStr, username, number;
        username = editProfileUsername.getText().toString();
        number = editProfileNumber.getText().toString();

        if(username.isEmpty()){
            editProfileUsername.requestFocus();
            helper.toastMessage("username field is empty");
        }else if(number.isEmpty()){
            editProfileNumber.requestFocus();
            helper.toastMessage("username field is empty");
        }else if(!profileImageUrl.isEmpty()){
            hashMap.put("profileImageUrl", profileImageUrl);
        }else{
            hashMap.put("username", username);
            hashMap.put("number", number);
        }

        databaseReference.child(currentUser.getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    helper.logMessage(ProfileFragment.class, "Data successfully uploaded");
                    helper.toastMessage("Data successfully uploaded");
                    helper.progressDialogEnd();
                }else{
                    helper.logMessage(ProfileFragment.class, "Error message: " + task.getException());
                    helper.toastMessage("Error message: " + task.getException());
                    helper.progressDialogEnd();
                }
            }
        });
    }

    // function with intent to get image and start activity for result...
    private void openSaveImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    //function to get file extention
    private String getFileExtention(){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    //upload image to firebase
    private void upLoadImage(){
        if(imageUri != null){
            profileImage.setImageURI(imageUri);

            //progress bar to upload profile image
            helper.progressDialogStart("Please Wait", "Uploading profile image");

            final StorageReference imageFileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtention());
            storageTask = imageFileReference.putFile(imageUri);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return imageFileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Uri uri = (Uri) task.getResult();
                        imageUriStr = uri.toString();
                        //TODO DONE: toast and log messages to describe event
                        helper.logMessage(ProfileFragment.class, "profile image successfully uploaded");
                        helper.toastMessage("profile image successfully uploaded");
                        helper.progressDialogEnd();

                    }else{
                        String errorMessage = task.getException().getMessage();
                        //TODO DONE: toast and log messages to describe event
                        helper.logMessage(ProfileFragment.class, "Error message: " + errorMessage);
                        helper.toastMessage("Error message: " + errorMessage);
                        helper.progressDialogEnd();
                    }
                }
            });
        }else{
            //TODO DONE: no image selected
            helper.toastMessage("image not selected");
        }
    }

    //code to call upload image function in activity for result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if(storageTask != null && storageTask.isInProgress()){
                //TODO DONE: toast message : upload in progress
                helper.toastMessage("image upload in progress");
            }else{
                upLoadImage();
            }
        }
    }

}
