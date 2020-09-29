package com.example.echat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.echat.Model.User;
import com.example.echat.Util.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;

public class DrawerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Helper helper;
    private DatabaseReference databaseReference;

    private ImageView profileImage;
    private TextView username;
    private String currentUser = "default";

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_users).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        helper = new Helper();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser().getUid();
            helper.toastMessage(this, "ID is " + currentUser);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

        View nav_view = navigationView.inflateHeaderView(R.layout.nav_header_drawer);
        profileImage = nav_view.findViewById(R.id.header_imageView);
        username = nav_view.findViewById(R.id.header_username);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    /*if(dataSnapshot.hasChild("username")){
                        username.setText(dataSnapshot.child("username").getValue().toString());
                    }*/
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    if(user.getProfileImageUrl().equals("default")){
                        profileImage.setImageResource(R.drawable.profile);
                    }else{
                        Glide.with(DrawerActivity.this).load(user.getProfileImageUrl()).into(profileImage);
                    }
                    helper.toastMessage(getApplicationContext(), user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                helper.gotoLoginActivity(getApplicationContext());
                return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            helper.gotoLoginActivity(getApplicationContext());
        }
    }

    public void status(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

        HashMap<String, Object> hashMap = new HashMap<>();
    }
}

//tools:showIn="@layout/app_bar_main"