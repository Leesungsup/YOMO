package com.example.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.talk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class SignupActivity extends AppCompatActivity {
    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseRemoteConfig mFirebaseRemoteConfig= FirebaseRemoteConfig.getInstance();
        String loading_background=mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);
        signup.setBackgroundColor(Color.parseColor(loading_background));
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString() == null || name.getText().toString() == null || password.getText().toString()==null){
                    return;
                }
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String uid = task.getResult().getUser().getUid();
                        UserModel userModel = new UserModel();
                        userModel.userName=name.getText().toString();
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                    }
                });
            }
        });
    }
}