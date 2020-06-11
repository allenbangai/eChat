package com.example.echat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.echat.Auth.AuthViewModel;
import com.example.echat.Util.Helper;

public class RegisterActivity extends AppCompatActivity {

    private Helper helper;
    private AuthViewModel authViewModel;
    private Button registerAccount, signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        helper = new Helper();
        authViewModel = new AuthViewModel();

        registerAccount = findViewById(R.id.register_button);
        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authViewModel.createAccount(v);
            }
        });

        signIn = findViewById(R.id.signInR);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.gotoLoginActivity(getApplicationContext());
                finish();
            }
        });
    }
}
