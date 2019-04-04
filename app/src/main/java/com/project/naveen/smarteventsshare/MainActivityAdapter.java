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

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.RecyclerViewHolder> {
    ArrayList<MainActivityData> mainActivityData = new ArrayList<MainActivityData>();
    private ItemClickListener mClickListener;
    public MainActivityAdapter(ArrayList<MainActivityData> mainActivityData) {
        this.mainActivityData = mainActivityData;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_row_layout,parent,false );

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view,mainActivityData);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final MainActivityData mainActivityData = this.mainActivityData.get(position);

        holder.tvName.setText(mainActivityData.getUser_name());
        holder.imageView.setImageURI(Uri.parse(mainActivityData.getImg_url()));
        holder.profileImg.setImageURI(Uri.parse(mainActivityData.getProfileImgUrl()));

        holder.tvCaption.setText(mainActivityData.getComment()+" @"+mainActivityData.getDate());
        holder.tvViews.setText(mainActivityData.getViews());
        holder.tvHearts.setText(mainActivityData.getLikes());
        holder.tvPostId.setText(mainActivityData.getPost_id());
        //holder.ivView.setVisibility(View.VISIBLE);
        /*
        holder.llHearts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hearts = Integer.parseInt(mainActivityData.getLikes());
                mainActivityData.setLikes(String.valueOf(hearts));
                notifyDataSetChanged();
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return mainActivityData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivView;
        TextView tvName,tvCaption,tvViews,tvHearts,tvPostId;
        SimpleDraweeView profileImg,imageView;
        LinearLayout llHearts;
        ArrayList<MainActivityData> mainActivityData = new ArrayList<MainActivityData>();

        public RecyclerViewHolder(View view,ArrayList<MainActivityData> mainActivityData) {
            super(view);

            this.mainActivityData = mainActivityData;

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
