package com.hardzei.mvvmmovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hardzei.mvvmmovies.R;
import com.hardzei.mvvmmovies.pojo.TrailerResult;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<TrailerResult> trailers;
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public TrailerAdapter() {
        this.trailers = new ArrayList<>();
    }

    private OnTrailerClickListener onTrailerClickListener;

    public void setTrailers(List<TrailerResult> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }

    public interface OnTrailerClickListener {
        void onTrailerClick(String url);
    }



    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.textViewNameOfVideo.setText(trailers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewNameOfVideo;

        public TrailerViewHolder(@NonNull final View itemView) {
            super(itemView);
            textViewNameOfVideo = itemView.findViewById(R.id.textViewNameOfVideo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onTrailerClickListener != null) {
                        onTrailerClickListener.onTrailerClick(BASE_YOUTUBE_URL + trailers.get(getAdapterPosition()).getKey());
                    }
                }
            });
    }
}
}
