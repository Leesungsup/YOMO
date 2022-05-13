package com.example.talk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    ArrayList<CategoryDomain> categoryDomains;

    public CategoryAdapter(ArrayList<CategoryDomain> categoryDomains) {
        this.categoryDomains = categoryDomains;
    }

    public interface OnItemClickEventListener{
        void OnItemClick(View a_view, int a_position);
    }

    private List<CategoryDomain> mItemList;
    private OnItemClickEventListener mItemClickListener = null;
    public CategoryAdapter(List<CategoryDomain> a_list){
        mItemList = a_list;
    }

    public void setOnItemClickListener(OnItemClickEventListener a_listener) {
        mItemClickListener = a_listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryName.setText(categoryDomains.get(position).getTitle());
        String picUrl="";
        switch (position) {
            case 0: {
                picUrl = "cat_1";
                break;
            }
            case 1: {
                picUrl = "cat_2";
                break;
            }
            case 2: {
                picUrl = "cat_3";
                break;
            }
            case 3: {
                picUrl = "cat_4";
                break;
            }
            case 4: {
                picUrl = "cat_5";
                break;
            }
        }

        int drawableReourceId = holder.itemView.getContext().getResources()
                .getIdentifier(picUrl, "drawable",
                        holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableReourceId)
                .into(holder.categoryPic);

    }


    @Override
    public int getItemCount() {
        return categoryDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryPic;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryPic = itemView.findViewById(R.id.categoryPic);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a_view) {
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        if(mItemClickListener!=null){
                            mItemClickListener.OnItemClick(a_view,position);
                        }
                    }
                }
            });
        }
    }
}

