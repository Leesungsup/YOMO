package com.example.talk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity {
    private Button signin;
    private Button signup;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        String loading_background=mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(loading_background));
        }
        signin=(Button)findViewById(R.id.loginActivity_button_signin);
        signup=(Button)findViewById(R.id.loginActivity_button_signup);
        signin.setBackgroundColor(Color.parseColor(loading_background));
        signup.setBackgroundColor(Color.parseColor(loading_background));
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }
}