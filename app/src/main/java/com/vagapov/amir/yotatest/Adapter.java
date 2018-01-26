package com.vagapov.amir.yotatest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

    private OpenLink openLink;
    private ArrayList<RocketModel> models;
    private Context context;
    private ImageDownloader imageDownloader;
    private LruCache<String, Bitmap> mLruCache;

    public LruCache<String, Bitmap> getLruCache() {
        return mLruCache;
    }

    Adapter(@NonNull ArrayList<RocketModel> models, @NonNull Context context, @NonNull OpenLink openLink, ImageDownloader imageDownloader) {
        this.models = models;
        this.context = context;
        this.openLink = openLink;
        this.imageDownloader = imageDownloader;
        mLruCache = new LruCache<>(18);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new Holder(view);
    }

    void setModels(ArrayList<RocketModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.onBind(models.get(position));
        if(mLruCache.get(models.get(position).getIcon()) == null) {
            imageDownloader.downloadImage(holder, models.get(position).getIcon());
        }else {
            holder.bindDownloadedImage(mLruCache.get(models.get(position).getIcon()));
        }
    }

    @Override
    public int getItemCount() {
        if (models != null) {
            return models.size();
        }
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView name;
        private TextView date;
        private ImageView icon;
        private TextView details;
        private String link;

        Holder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_name);
            date = itemView.findViewById(R.id.text_view_time);
            details = itemView.findViewById(R.id.text_view_details);
            icon = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
        }

        void onBind(final RocketModel model) {
            name.setText(model.getName());
            date.setText(model.getDate());
            details.setText(model.getDetails());
            link = model.getArticleLink();
        }

        @Override
        public void onClick(View view) {
            openLink.openLink(link);
        }

        void bindDownloadedImage(Bitmap bitmap){
            icon.setImageBitmap(bitmap);
        }

    }
}
