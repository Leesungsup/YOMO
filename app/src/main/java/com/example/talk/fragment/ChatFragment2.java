package com.example.talk.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.talk.CategoryAdapter;
import com.example.talk.CategoryDomain;
import com.example.talk.R;
import com.example.talk.chat.GroupMessageActivity;
import com.example.talk.chat.MessageActivity;
import com.example.talk.model.ChatModel;
import com.example.talk.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;


public class ChatFragment2 extends Fragment {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");
    private RecyclerView recyclerViewCategotyList;
    private CategoryAdapter adapter;
    private DatabaseReference mDatabase;
    String menu="defaults";
    String selectedMenu=null;
    String destinationRoom;
    private String uid;
    int lev;
    Intent intent1;
    FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat2, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chatfragment_recyclerview2);
        recyclerView.setAdapter(new ChatFragment2.ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        //?????? ???????????? ??????
        recyclerViewCategotyList = view.findViewById(R.id.view1);
        ArrayList<CategoryDomain> categoryList = new ArrayList<>();
        categoryList.add(new CategoryDomain("??????", "cat_1"));
        categoryList.add(new CategoryDomain("??????", "cat_2"));
        categoryList.add(new CategoryDomain("??????", "cat_3"));
        categoryList.add(new CategoryDomain("??????", "cat_4"));
        categoryList.add(new CategoryDomain("??????", "cat_5"));
        adapter = new CategoryAdapter(categoryList);
        recyclerViewCategotyList.setAdapter(adapter);
        //?????????????????? ???????????????
        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickEventListener() {
            @Override
            public void OnItemClick(View a_view, int a_position) {
                //item?????? ?????????????????? ?????? ??????
                final CategoryDomain item = categoryList.get(a_position);
                selectedMenu = item.getTitle();
                Toast.makeText(a_view.getContext(), selectedMenu+"Clicked", Toast.LENGTH_SHORT).show();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ChatFragment3 fragment3 = new ChatFragment3();
                Bundle bundle = new Bundle();
                bundle.putString("selectedMenu",selectedMenu);
                fragment3.setArguments(bundle);
                transaction.replace(R.id.mainactivity_framelayout, fragment3);
                transaction.commit();


            }
        });
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingButton);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                //String value = dataSnapshot.getValue(String.class);
                lev=userModel.level;
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(lev>=5) {
                                startActivity(new Intent(view.getContext(), SelectFriendActivity.class));
                            }else{
                                Toast.makeText(getActivity(),"level??? ???????????????.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }



    /*private void recyclerViewCategory() {
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategotyList = getView().findViewById(R.id.view1);
        //recyclerViewCategotyList.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryDomain> categoryList = new ArrayList<>();
        categoryList.add(new CategoryDomain("??????", "cat_1"));
        categoryList.add(new CategoryDomain("??????", "cat_2"));
        categoryList.add(new CategoryDomain("??????", "cat_3"));
        categoryList.add(new CategoryDomain("??????", "cat_4"));
        categoryList.add(new CategoryDomain("??????", "cat_5"));

        adapter = new CategoryAdapter(categoryList);
        recyclerViewCategotyList.setAdapter(adapter);
    }*/

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ChatModel> chatModels = new ArrayList<>();
        private List<String> keys = new ArrayList<>();
        private ArrayList<String> destinationUsers = new ArrayList<>();

        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //mDatabase=FirebaseDatabase.getInstance().getReference();
            //Log.e("3333333","fasgagw"+mDatabase.child("chatrooms"));
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        chatModels.add(item.getValue(ChatModel.class));
                        keys.add(item.getKey());
                    notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

//            //Firebase after filter                  //?????? ??????!!!! chatrooms -> menu ?    menu -> selectedMenu
//            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild(menu).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    chatModels.clear();
//                    for (DataSnapshot item :dataSnapshot.getChildren()){
//                        chatModels.add(item.getValue(ChatModel.class));
//                        keys.add(item.getKey());
//                    }
//                    notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);


            return new ChatFragment2.ChatRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final ChatFragment2.ChatRecyclerViewAdapter.CustomViewHolder customViewHolder = (ChatFragment2.ChatRecyclerViewAdapter.CustomViewHolder) holder;
            String destinationUid = null;

            // ?????? ????????? ?????? ????????? ??????
            for (String user : chatModels.get(position).users.keySet()) {
                if (!user.equals(uid)) {
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            if(destinationUid!=null) {
                FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        Glide.with(customViewHolder.itemView.getContext())
                                .load(userModel.profileImageUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(customViewHolder.imageView);
                        //customViewHolder.textView_title.setText(userModel.userName);
                        customViewHolder.textView_title.setText(chatModels.get(position).title);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            //???????????? ?????? ???????????? ?????? ??? ????????? ???????????? ????????? ?????????
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            if (commentMap.keySet().toArray().length > 0) {
                String lastMessageKey = (String) commentMap.keySet().toArray()[0];
                customViewHolder.textView_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).message);


                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
                Date date = new Date(unixTime);
                customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
            }
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    if (chatModels.get(position).users.size() >= 1) {
                        intent = new Intent(view.getContext(), GroupMessageActivity.class);
                        intent.putExtra("destinationRoom", keys.get(position));
                    } else {
                        intent = new Intent(view.getContext(), MessageActivity.class);
                        intent.putExtra("destinationUid", destinationUsers.get(position));
                    }

                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }

                    /*ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright,R.anim.toleft);
                        startActivity(intent,activityOptions.toBundle());
                    }*/

                }
            });
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last_message;
            public TextView textView_timestamp;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.chatitem_imageview);
                textView_title = (TextView) view.findViewById(R.id.chatitem_textview_title);
                textView_last_message = (TextView) view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_timestamp = (TextView) view.findViewById(R.id.chatitem_textview_timestamp);
            }
        }


    }
}