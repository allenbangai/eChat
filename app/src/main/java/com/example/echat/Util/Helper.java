package com.example.echat.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.echat.DrawerActivity;
import com.example.echat.LogInActivity;
import com.example.echat.MainActivity;
import com.example.echat.RegisterActivity;

public class Helper{
    Intent intent;
    Context context;
    private ProgressDialog progressDialog;

    public Helper(){

    }

    public Helper(Context context){
        this.context = context;
    }

    //progress dialog functions
    public void progressDialogStart(String titleMessage, String detailMessage){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(titleMessage);
        progressDialog.setMessage(detailMessage);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
    }

    public void progressDialogEnd(){
        progressDialog.dismiss();
    }


    public void toastMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void gotoLoginActivity(Context context){
        intent = new Intent(context, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void gotoRegisterActivity(Context context){
        intent = new Intent(context, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void gotoMainActivity(Context context){
        intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void gotoDrawerActivity(Context context){
        intent = new Intent(context, DrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /*<item name="android:statusBarColor">@android:color/transparent</item>
    */
}
