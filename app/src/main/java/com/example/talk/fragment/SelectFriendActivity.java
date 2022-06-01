package com.example.talk.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.talk.R;
import com.example.talk.chat.MessageActivity;
import com.example.talk.model.ChatModel;
import com.example.talk.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendActivity extends AppCompatActivity {
    ChatModel chatModel = new ChatModel();
    int lev;
    String myUid;
    int value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selectFriendActivity_recyclerview);
        //recyclerView.setAdapter(new SelectFriendRecyclerViewAdapter());
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button button = (Button) findViewById(R.id.selectFriendActivity_button);
        EditText title = (EditText) findViewById(R.id.groupMessageActivity_title);
        RadioGroup colorType = findViewById(R.id.radio_menu);
        //라디오버튼 체크시 이벤트
        colorType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radio_1)
                {
                    chatModel.menu="피자";
                }else if(checkedId == R.id.radio_2)
                {
                    chatModel.menu="양식";
                }else if(checkedId == R.id.radio_3)
                {
                    chatModel.menu="치킨";
                }else if(checkedId == R.id.radio_4)
                {
                    chatModel.menu="한식";
                }else if(checkedId == R.id.radio_5)
                {
                    chatModel.menu="카페";
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatModel.users.put(myUid,true);
                chatModel.users.put("N1OdwdxHvYRr0iUhJNfD4aUWsG63",true);
                chatModel.title=title.getText().toString();
                chatModel.host=myUid;
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
                //startActivity(new Intent(view.getContext(), ChatFragment2.class));
                getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment2()).commit();

            }
        });
    }
    class SelectFriendRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<UserModel> userModels;

        public SelectFriendRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModels.clear();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if(userModel.uid.equals(myUid)){
                            continue;
                        }
                        userModels.add(userModel);
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_select,parent,false);


            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Glide.with(holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userModels.get(position).userName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    if(intent!=null) {
                        intent.putExtra("destinationUid", userModels.get(position).uid);
                        ActivityOptions activityOptions = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                            startActivity(intent, activityOptions.toBundle());
                        }
                    }

                }
            });
            if(userModels.get(position).comment != null){
                ((CustomViewHolder) holder).textView_comment.setText(userModels.get(position).comment);
            }
            ((CustomViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //체크 된상태
                    if(b){
                        chatModel.users.put(userModels.get(position).uid,true);
                        //체크 취소 상태
                    }else{
                        chatModel.users.remove(userModels.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;
            public CheckBox checkBox;
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_comment = (TextView) view.findViewById(R.id.frienditem_textview_comment);
                checkBox=(CheckBox) view.findViewById(R.id.frienditem_checkbox);
            }
        }
    }
}