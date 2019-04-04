package com.project.naveen.smarteventsshare;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class AllCategoryAdapter extends RecyclerView.Adapter<AllCategoryAdapter.RecyclerViewHolder> {
    ArrayList<AllCategoryData> allCategoryData = new ArrayList<AllCategoryData>();
    private ItemClickListener mClickListener;
    public AllCategoryAdapter(ArrayList<AllCategoryData> allCategoryData) {
        this.allCategoryData = allCategoryData;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_row_layout,parent,false );

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view,allCategoryData);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final AllCategoryData allCategoryData = this.allCategoryData.get(position);

        holder.tvName.setText(allCategoryData.getUser_name());
        holder.imageView.setImageURI(Uri.parse(allCategoryData.getImg_url()));
        holder.profileImg.setImageURI(Uri.parse(allCategoryData.getProfileImgUrl()));

        holder.tvCaption.setText(allCategoryData.getComment()+" @"+allCategoryData.getDate());
        holder.tvViews.setText(allCategoryData.getViews());
        holder.tvHearts.setText(allCategoryData.getLikes());
        holder.tvPostId.setText(allCategoryData.getPost_id());
        //holder.ivView.setVisibility(View.VISIBLE);
        /*
        holder.llHearts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hearts = Integer.parseInt(allCategoryData.getLikes());
                allCategoryData.setLikes(String.valueOf(hearts));
                notifyDataSetChanged();
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return allCategoryData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivView;
        TextView tvName,tvCaption,tvViews,tvHearts,tvPostId;
        SimpleDraweeView profileImg,imageView;
        LinearLayout llHearts;
        ArrayList<AllCategoryData> allCategoryData = new ArrayList<AllCategoryData>();

        public RecyclerViewHolder(View view,ArrayList<AllCategoryData> allCategoryData) {
            super(view);

            this.allCategoryData = allCategoryData;

            imageView = ButterKnife.findById(view,R.id.imageView);
            tvName = view.findViewById(R.id.tvName);
            profileImg = ButterKnife.findById(view,R.id.profileImg);

            tvCaption = view.findViewById(R.id.tvCaption);
            tvViews = view.findViewById(R.id.tvViews);
            tvHearts = view.findViewById(R.id.tvHearts);
            tvPostId = view.findViewById(R.id.tvPostId);
            llHearts = view.findViewById(R.id.llHearts);
            llHearts.setOnClickListener(this);
            //ivView = view.findViewById(R.id.ivView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
