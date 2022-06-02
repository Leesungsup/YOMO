package com.example.talk.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.talk.R;
import com.example.talk.model.ChatModel;
import com.example.talk.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class EvaluationActivity extends AppCompatActivity {
    private RatingBar ratingbar;
    Button submit;
    String hostname;
    Query select;
    int lev1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        Intent intent = getIntent();
        String destinationRoom =intent.getExtras().getString("destinationRoom");
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                //String value = dataSnapshot.getValue(String.class);
                hostname =chatModel.host;
                FirebaseDatabase.getInstance().getReference().child("users").child(hostname).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        //String value = dataSnapshot.getValue(String.class);
                        lev1 =userModel.level;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.e("3333","msg"+hostname);

        ratingbar = findViewById(R.id.ratingbarStyle);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                lev1= (int) (lev1+ratingbar.getRating());
                FirebaseDatabase.getInstance().getReference().child("users").child(hostname).child("level").removeValue();
                FirebaseDatabase.getInstance().getReference().child("users").child(hostname).child("level").setValue(lev1);
                Toast.makeText(getApplicationContext(),"평가완료",Toast.LENGTH_SHORT).show();
            }
        });
    }
}