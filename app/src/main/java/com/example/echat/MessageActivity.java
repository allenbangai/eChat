package com.example.echat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.FileReader;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView username;
    private ImageButton send_button;
    private EditText text_message;
    private Intent intent;

    private Helper helper;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        helper = new Helper();
        profileImage = findViewById(R.id.message_profileImage);
        username = findViewById(R.id.message_username);
        send_button = findViewById(R.id.button_send_message);
        text_message = findViewById(R.id.enter_text_message);
        intent = getIntent();
        final String userID = intent.getStringExtra("userID");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        helper.toastMessage(this, "user ID is: " + userID);

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

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = text_message.getText().toString();
                if(!message.trim().isEmpty()){
                    send_message(currentUser.getUid(), userID, message);
                }
                text_message.setText("");
            }
        });
    }

    private void send_message(String sender, String receiver, String message){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        data.child("Chats").push().setValue(hashMap);

    }

}