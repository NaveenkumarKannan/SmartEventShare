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

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.RecyclerViewHolder> {
    ArrayList<AllActivityData> allActivityData = new ArrayList<AllActivityData>();
    private ItemClickListener mClickListener;
    public AllAdapter(ArrayList<AllActivityData> allActivityData) {
        this.allActivityData = allActivityData;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_row_layout,parent,false );

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, allActivityData);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final AllActivityData allActivityData = this.allActivityData.get(position);

        holder.imageView.setImageURI(Uri.parse(allActivityData.getImg_url()));
        holder.tvViews.setText(allActivityData.getViews());
        holder.tvHearts.setText(allActivityData.getLikes());
    }

    @Override
    public int getItemCount() {
        return allActivityData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView tvViews,tvHearts;
        ArrayList<AllActivityData> allActivityData = new ArrayList<AllActivityData>();

        public RecyclerViewHolder(View view,ArrayList<AllActivityData> allActivityData) {
            super(view);

            this.allActivityData = allActivityData;

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

