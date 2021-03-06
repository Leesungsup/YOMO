package com.example.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity {
    private Button signin;
    private TextView signup;
    private FirebaseRemoteConfig FirebaseRemoteConfig;
    private EditText id;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id = (EditText)findViewById(R.id.loginActivity_edittext_id);
        password=(EditText)findViewById(R.id.loginActivity_edittext_password);
        FirebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        String loading_background=FirebaseRemoteConfig.getString(getString(R.string.rc_color));
        //일정 버전 이상만 사용가능하도록
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(loading_background));
        }
        signin=(Button)findViewById(R.id.loginActivity_button_signin);
        signup=(TextView) findViewById(R.id.loginActivity_button_signup);
        signin.setBackgroundColor(Color.parseColor(loading_background));
        signup.setBackgroundColor(Color.parseColor(loading_background));
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginEvent();
            }
        });
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null && firebaseAuth.getCurrentUser().isEmailVerified()){
                    //로그인
                    Intent intent = new Intent(LoginActivity.this,MainActivity2.class);
                    if(intent!=null) {
                        startActivity(intent);
                        finish();
                    }
                }else if(user != null){
                    //로그아웃
                    Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                }else{

                }


            }
        };
    }
    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                //startActivity(new Intent(LoginActivity.this, MainActivity2.class));
                            }else{
                                //로그인 실패한부분.
                                //Toast.makeText(LoginActivity.this, "Please 2222verify your email", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
