package com.example.talk.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.talk.R;
import com.example.talk.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity {
    public List<String> keys = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        Intent intent = getIntent();
        String a =intent.getExtras().getString("destinationRoom");

        PeopleFragment fragment = new PeopleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("destinationRoom",a);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.peopleactivity_framelayout,fragment).commit();


    }


}