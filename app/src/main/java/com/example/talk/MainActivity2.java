package com.example.talk;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import com.example.talk.fragment.PeopleFragment;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new PeopleFragment()).commit();
    }
}