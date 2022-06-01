package com.example.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import com.example.talk.fragment.AccountFragment;
import com.example.talk.fragment.ChatFragment;
import com.example.talk.fragment.ChatFragment2;
import com.example.talk.fragment.PeopleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    //메인화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainactivity_bottomnavigationview);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment2()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_people:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment2()).commit();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new PeopleFragment()).commit();
                        return true;
                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment()).commit();
                        return true;
                    case R.id.action_account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new AccountFragment()).commit();
                        return true;
                }

                return false;
            }
        });
        passPushTokenToServer();
    }
    void passPushTokenToServer(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);


    }
}