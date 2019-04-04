package com.project.naveen.smarteventsshare;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.RecyclerViewHolder> {
    ArrayList<TrendData> trendData = new ArrayList<TrendData>();
    private ItemClickListener mClickListener;
    public TrendAdapter(ArrayList<TrendData> trendData) {
        this.trendData = trendData;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_row_layout,parent,false );

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view,trendData);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final TrendData trendData = this.trendData.get(position);

        holder.imageView.setImageURI(Uri.parse(trendData.getImg_url()));
        holder.tvViews.setText(trendData.getViews());
        holder.tvHearts.setText(trendData.getLikes());
    }

    @Override
    public int getItemCount() {
        return trendData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView tvViews,tvHearts;
        ArrayList<TrendData> trendData = new ArrayList<TrendData>();

        public RecyclerViewHolder(View view,ArrayList<TrendData> trendData) {
            super(view);

            this.trendData = trendData;

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

