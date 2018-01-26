package com.vagapov.amir.yotatest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoadingInterface, OpenLink {

    private static final String API_URL = "https://api.spacexdata.com/v2/launches?launch_year=2017";
    private ProgressBar progressBar;
    private ArrayList<RocketModel> rocketModels = new ArrayList<>();
    private Adapter adapter;

    private ImageDownloader<Adapter.Holder> mImageDownloader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            rocketModels = (ArrayList<RocketModel>) savedInstanceState.getSerializable(getString(R.string.rocket_model));
        }
        setContentView(R.layout.activity_main);


        Handler mainHandler = new Handler();

        mImageDownloader = new ImageDownloader<>(mainHandler, new ImageDownloader.CallbackDownloadImage<Adapter.Holder>() {
            @Override
            public void setImageOnMainThread(Bitmap bitmap, Adapter.Holder holder, String url) {
                holder.bindDownloadedImage(bitmap);
                adapter.getLruCache().put(url, bitmap);
            }
        });
        mImageDownloader.start();
        mImageDownloader.getLooper();

        initUI();
        if(rocketModels.isEmpty()) {
            startLoading();
        }

    }

    private void initUI() {
        progressBar = findViewById(R.id.progress_bar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(rocketModels, this, this, mImageDownloader);
        recyclerView.setAdapter(adapter);
    }

    private void startLoading() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPage(this).execute(API_URL);
        } else {
            Toast.makeText(this, R.string.check_out_internet, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void loading(boolean b) {
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(getString(R.string.rocket_model), rocketModels);
    }

    @Override
    public void setModels(@NonNull ArrayList<RocketModel> models) {
        rocketModels = models;
        adapter.setModels(models);
    }

    @Override
    public void openLink(@NonNull String link) {
        Uri address = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, address);
        Intent intentChooser = Intent.createChooser(intent, getString(R.string.choose_app));
        startActivity(intentChooser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageDownloader.clear();
    }
}
