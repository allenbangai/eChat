package com.example.echat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.echat.Model.User;
import com.example.echat.Util.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView username;
    private Intent intent;

    private Helper helper;
    private FirebaseUser user;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar1 = findViewById(R.id.toolbar01);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        helper = new Helper();
        profileImage = findViewById(R.id.user_profile_image);
        username = findViewById(R.id.user_username);
        intent = getIntent();
        String userID = intent.getStringExtra("userID");

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getProfileImageUrl().equals("default")){
                    profileImage.setImageResource(R.drawable.profile);
                }else{
                    Glide.with(MessageActivity.this).load(user.getProfileImageUrl()).into(profileImage);
                }
                helper.toastMessage(getApplicationContext(), user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}