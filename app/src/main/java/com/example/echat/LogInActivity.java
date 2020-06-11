package com.example.echat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.echat.Auth.AuthViewModel;
import com.example.echat.Util.Helper;

public class LogInActivity extends AppCompatActivity {
    private Helper helper;
    private AuthViewModel authViewModel;

    private Button gotoRegister, signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        helper = new Helper();
        authViewModel = new AuthViewModel();

        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authViewModel.logIn(v);
            }
        });

        gotoRegister = findViewById(R.id.register_button_1);
        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.gotoRegisterActivity(getApplicationContext());
            }
        });

    }
}
