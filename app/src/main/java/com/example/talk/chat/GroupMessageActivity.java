package com.example.talk.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.talk.R;
import com.example.talk.fragment.ChatFragment;
import com.example.talk.fragment.ChatFragment2;
import com.example.talk.fragment.EvaluationActivity;
import com.example.talk.fragment.PeopleActivity;
import com.example.talk.fragment.SelectFriendActivity;
import com.example.talk.model.ChatModel;
import com.example.talk.model.NotificationModel;
import com.example.talk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupMessageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Map<String, UserModel> users = new HashMap<>();
    String destinationRoom;
    String uid;
    EditText editText;
    Toolbar myToolbar;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private RecyclerView recyclerView;
    int peopleCounter=0;
    final String[] hostname = new String[1];
    List<ChatModel.Comment> comments = new ArrayList<>();
    Map<String, Object> updateusersMap = new HashMap<>();
    String chatTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        destinationRoom=getIntent().getStringExtra("destinationRoom");
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText=(EditText)findViewById(R.id.groupMessageActivity_editText);
        DrawerLayout drawer = findViewById(R.id.chat_drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        myToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(myToolbar);

        navigationView.setNavigationItemSelectedListener(this);
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = (Map<String, UserModel>) dataSnapshot.getValue();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    users.put(item.getKey(),item.getValue(UserModel.class));
                }
                init();
                recyclerView = (RecyclerView) findViewById(R.id.groupMessageActivity_recyclerview);
                recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                //String value = dataSnapshot.getValue(String.class);
                hostname[0] =chatModel.host;
                chatTitle=chatModel.title;
                getSupportActionBar().setTitle(chatTitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if (item.getKey().equals(uid)) {
                        continue;
                    }
                    updateusersMap.put(item.getKey(), true);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    void init(){
        Button button = (Button) findViewById(R.id.groupMessageActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        editText.setText("");
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> usersMap = new HashMap<>();
                                for(DataSnapshot item : dataSnapshot.getChildren()){
                                    //usersMap.put(item.getKey(),item.getValue(UserModel.class));
                                    usersMap.put(item.getKey(),true);
                                }
                                usersMap.put(uid, true);
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").updateChildren(usersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        });

    }
    /*void sendGcm(String pushToken) {

        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        //notificationModel.data.title = userName;
        //notificationModel.data.text = editText.getText().toString();


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyDNFs9vhpZQzQ3VeMXojsAJIId2Z7aj_Xk")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }*/
    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public GroupMessageRecyclerViewAdapter(){

            getMessageList();
        }
        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        comment_motify.readUsers.put(uid, true);

                        readUsersMap.put(key, comment_motify);
                        comments.add(comment_origin);
                    }
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size() - 1);

                    /*if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {


                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1);
                            }
                        });
                    } else {
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size() - 1);
                    }*/
                    //메세지가 갱신


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);


            return new GroupMessageViewHodler(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GroupMessageViewHodler messageViewHolder = ((GroupMessageViewHodler) holder);

            //내가보낸 메세지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);

                //상대방이 보낸 메세지

            } else {

                Glide.with(holder.itemView.getContext())
                        .load(users.get(comments.get(position).uid).profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textview_name.setText(users.get(comments.get(position).uid).userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);


            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }
        void setReadCounter(final int position, final TextView textView){

            if(peopleCounter==0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peopleCounter=users.size();
                        int count = peopleCounter - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                int count = peopleCounter - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }

        }
        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupMessageViewHodler extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textview_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            public GroupMessageViewHodler(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textview_name = (TextView)view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = (ImageView)view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView)view.findViewById(R.id.messageItem_textview_timestamp);
                textView_readCounter_left=(TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right=(TextView) view.findViewById(R.id.messageItem_textview_readCounter_right);

            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_chat_toolbar,menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId())
        {
            case R.id.toolbar_menu:
                DrawerLayout drawer = findViewById(R.id.chat_drawer);
                drawer.openDrawer(GravityCompat.END);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            //case R.id.drawer_members:
                //Intent intent=new Intent(GroupMessageActivity.this, PeopleActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //intent.putExtra("destinationRoom", destinationRoom);
                //GroupMessageActivity.this.startActivity(intent);
                //break;

            case R.id.drawer_finaltest:
                Intent intent1=new Intent(GroupMessageActivity.this, EvaluationActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("destinationRoom", destinationRoom);
                GroupMessageActivity.this.startActivity(intent1);
                break;

            case R.id.drawer_exit:
                exitproject();
                //break;
        }

        DrawerLayout drawer = findViewById(R.id.chat_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void exitproject(){

        if (uid.equals(hostname[0])) {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).removeValue();
            //startActivity(new Intent(v.getContext(), ChatFragment2.class));
            getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment2()).commit();
        } else {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").removeValue();
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").updateChildren(updateusersMap);
            //startActivity(new Intent(v.getContext(), ChatFragment.class));
            getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment()).commit();

            //FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").removeValue();
            //startActivity(new Intent(v.getContext(), ChatFragment.class));
        }
    }
}
