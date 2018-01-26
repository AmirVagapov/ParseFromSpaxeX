package com.vagapov.amir.yotatest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class DownloadPage extends AsyncTask<String, Void, ArrayList<RocketModel>>{

    private LoadingInterface loadingInterface;

    DownloadPage(LoadingInterface callBackProgressBar) {
        this.loadingInterface = callBackProgressBar;
    }

    @Override
    protected void onPreExecute() {
        loadingInterface.loading(true);
    }

    @Override
    protected void onPostExecute(ArrayList<RocketModel> models) {
        loadingInterface.loading(false);
        loadingInterface.setModels(models);
    }

    @Override
    protected ArrayList<RocketModel> doInBackground(String... urls) {
        try {
           return downloadUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @NonNull
    private ArrayList<RocketModel> downloadUrl(String urls) throws IOException {
        InputStream inputStream = null;
        String data = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urls);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                byte[] result = bos.toByteArray();
                bos.close();
                data = new String(result);
            } else {
                data = connection.getResponseMessage() + ". Error code: " + responseCode;
            }
            connection.disconnect();
        }catch (Exception exc){
            exc.printStackTrace();
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
            if (connection != null){
                connection.disconnect();
            }
        }
        return new RocketsParser().parseData(data);

    }
}
