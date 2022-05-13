//package com.example.talk.fragment;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.Filter;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.talk.R;
//import com.example.talk.model.FilterModel;
//import com.example.talk.model.UserModel;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SelectFilterActivity  extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_filter);
//
//        RecyclerView recyclerView = findViewById(R.id.selectFilterActivity_recyclerView);
//        ArrayList<FilterModel> list = new ArrayList<>();
//        list.add(new FilterModel("치킨"));
//        list.add(new FilterModel("한식"));
//        list.add(new FilterModel("중식"));
//        list.add(new FilterModel("일식"));
//        list.add(new FilterModel("양식"));
//
//        SelectFilterRecyclerViewAdapter adapter = new SelectFilterRecyclerViewAdapter(this, list, recyclerView);
//
//        Button button = (Button) findViewById(R.id.assign);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//    }
//
//
//
//    class SelectFilterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//        Context context;
//        ArrayList<FilterModel> list;
//        RecyclerView recyclerView;
//
//        public SelectFilterRecyclerViewAdapter(Context context, ArrayList<FilterModel> list, RecyclerView recyclerView){
//            this.context = context;;
//            this.list = list;
//            this.recyclerView = recyclerView;
//        }
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_selected, parent, false);
//            return new CustomViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            FilterModel filterModel = list.get(position);
//            ((CustomViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    //checked
//                    if(b){//store data in firebase -> 채팅방 list를 보여줄 때 확인할 수 있도록
//                        //((CustomViewHolder) holder).menu.setText(filterModel.getMenu());
//                    }
//                    //nothing checked
//                    else{
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() { return list.size(); }
//    }
//
//
//    private class CustomViewHolder extends RecyclerView.ViewHolder {
//        CheckBox checkBox;
//        TextView menu;
//        public CustomViewHolder(View itemView) {
//            super(itemView);
//            this.checkBox=itemView.findViewById(R.id.filter_checkbox);
//            this.menu=itemView.findViewById(R.id.filter_tx);
//        }
//    }
//}
