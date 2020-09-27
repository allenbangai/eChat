package com.example.echat.ui.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.echat.Model.User;
import com.example.echat.R;
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
    private CircleImageView profileImage, saveImage, editImage;
    private TextView profileEmail, profileUserName, profileNumber;
    private EditText editProfileEmail, editProfileUsername, editProfileNumber;

    private ProfileViewModel profileViewModel;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

    private Uri imageUri;
    private StorageTask storageTask;
    private String imageUriStr = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child(currentUser.getUid());

        profileImage = root.findViewById(R.id.profile_image);
        saveImage = root.findViewById(R.id.save_profile_info);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveprofileInfo();
            }
        });
        editImage = root.findViewById(R.id.edit_profile_info);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibiltyEdit();
            }
        });

        profileUserName = root.findViewById(R.id.profile_username);
        profileEmail = root.findViewById(R.id.profile_email);
        profileNumber = root.findViewById(R.id.profile_number);

        editProfileEmail = root.findViewById(R.id.profile_email_edit);
        editProfileNumber = root.findViewById(R.id.profile_number_edit);
        editProfileUsername = root.findViewById(R.id.profile_username_edit);

        loadProfileInfo();


        return root;
    }

    private void changeVisibiltyEdit() {
        profileNumber.setVisibility(View.GONE);
        profileUserName.setVisibility(View.GONE);
        editImage.setVisibility(View.GONE);

        editProfileUsername.setVisibility(View.VISIBLE);
        editProfileNumber.setVisibility(View.VISIBLE);
        saveImage.setVisibility(View.VISIBLE);
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(snapshot.exists()){
                    profileUserName.setText(user.getUsername());
                    profileEmail.setText(user.getEmail());
                    profileNumber.setText(user.getNumber());

                    if(!user.getProfileImageUrl().equals("default")){
                        Glide.with(getContext()).load(user.getProfileImageUrl()).into(profileImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //function to save profile info online to firebase
    private void saveprofileInfo(){

        HashMap<String, String> hashMap  = new HashMap<>();
        String profileImageUrl = imageUriStr, username, number;
        username = editProfileUsername.getText().toString();
        number = editProfileNumber.getText().toString();

        if(username.isEmpty()){

        }else if(number.isEmpty()){

        }else if(profileImageUrl.isEmpty()){

        }else{
            hashMap.put("username", username);
            hashMap.put("number", number);
            hashMap.put("profileImageUrl", profileImageUrl);
        }

        databaseReference.child(currentUser.getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadProfileInfo();
                    changeVisibiltySave();
                }else{

                }
            }
        });
    }

    private void openSaveImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtention(){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    //upload image to firebase
    private void upLoadImage(){

        if(imageUri != null){
            profileImage.setImageURI(imageUri);
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
                        //TODO: toast and log messages to describe event

                    }else{
                        String errorMessage = task.getException().getMessage();
                        //TODO: toast and log messages to describe event
                    }
                }
            });
        }else{
            //TODO: no image selected
        }
    }

    //code to call upload image function in activity for result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if(storageTask != null && storageTask.isInProgress()){
                //TODO : toast message : upload in progress
            }else{
                upLoadImage();
            }
        }
    }
}
