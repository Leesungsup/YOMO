package com.example.talk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.talk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBAM=10;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    private ImageView profile;
    private Uri imageUri;
    private String loading_background;
    private String pathUri;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseRemoteConfig mFirebaseRemoteConfig= FirebaseRemoteConfig.getInstance();
        loading_background=mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        profile = (ImageView) findViewById(R.id.signupActivity_imageview_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBAM);
            }
        });
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);
        signup.setBackgroundColor(Color.parseColor(loading_background));
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //email, 이름, 비밀번호, 사진이 있는지 확인
                if(email.getText().toString() == null || name.getText().toString() == null || password.getText().toString()==null || imageUri==null){
                    return;
                }
                //회원가입
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //uid 받아오기
                            final String uid = task.getResult().getUser().getUid();
                            /*final Uri file = Uri.fromFile(new File(pathUri));
                            StorageReference storageReference = mStorage.getReference()
                                    .child("usersprofileImages").child("uid/"+file.getLastPathSegment());
                            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                                    while (!imageUrl.isComplete()) ;

                                    UserModel userModel = new UserModel();

                                    userModel.userName = name.getText().toString();
                                    userModel.uid = uid;
                                    userModel.profileImageUrl = imageUrl.getResult().toString();

                                    // database에 저장
                                    mDatabase.getReference().child("users").child(uid)
                                            .setValue(userModel);
                                }

                            });*/
                            //firebase realdatabase에 이미지주소 저장
                            FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    @SuppressWarnings("VisibleForTests")
                                    Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                                    while (!imageUrl.isComplete()) ;
                                    UserModel userModel = new UserModel();
                                    userModel.userName = name.getText().toString();
                                    userModel.profileImageUrl = imageUrl.getResult().toString();
                                    userModel.uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    userModel.level=1;
                                    //사용자 정보 realdatabase에 저장
                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>(){

                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            SignupActivity.this.finish();
                                        }
                                    });

                                }
                            });
                        }else {
                            if (task.getException() != null) { // 회원가입 실패
                                Log.d("fdf", "createUserWithEmail:failure");
                            }
                        }
                    }
                });
            }
        });
    }
    //사진선택
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBAM && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData());
            imageUri = data.getData();
            pathUri = getPath(data.getData());
        }
    }
    // uri 절대경로 가져오기
    public String getPath(Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(index);
    }
}