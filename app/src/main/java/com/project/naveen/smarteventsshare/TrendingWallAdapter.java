package com.project.naveen.smarteventsshare;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.ButterKnife;
public class TrendingWallAdapter extends RecyclerView.Adapter<TrendingWallAdapter.RecyclerViewHolder> {
    ArrayList<TrendingWallData> trendingWallData = new ArrayList<TrendingWallData>();
    private ItemClickListener mClickListener;
    public TrendingWallAdapter(ArrayList<TrendingWallData> trendingWallData) {
        this.trendingWallData = trendingWallData;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_wall_row_layout,parent,false );

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view,trendingWallData);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final TrendingWallData trendingWallData = this.trendingWallData.get(position);

        holder.imageView.setImageURI(Uri.parse(trendingWallData.getImg_url()));
        holder.tvViews.setText(trendingWallData.getViews());
        holder.tvHearts.setText(trendingWallData.getLikes());
    }

    @Override
    public int getItemCount() {
        return trendingWallData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView tvViews,tvHearts;
        ArrayList<TrendingWallData> trendingWallData = new ArrayList<TrendingWallData>();

        public RecyclerViewHolder(View view,ArrayList<TrendingWallData> trendingWallData) {
            super(view);

            this.trendingWallData = trendingWallData;

            view.setOnClickListener(this);
            imageView = ButterKnife.findById(view,R.id.imageView);
            tvViews = view.findViewById(R.id.tvViews);
            tvHearts = view.findViewById(R.id.tvHearts);
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

