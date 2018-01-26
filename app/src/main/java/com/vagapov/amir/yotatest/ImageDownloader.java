package com.vagapov.amir.yotatest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class ImageDownloader<T> extends HandlerThread {

    private Handler imageDownloadHandler;
    private static final int WHAT = 0;
    private Handler mainHandler;

    private boolean hasQuit = false;

    private static final  String name = "downloadImage";
    private CallbackDownloadImage mCallbackDownloadImage;

    private ConcurrentMap<T, String> urlAdapterListMap = new ConcurrentHashMap<>();

    public interface CallbackDownloadImage<T> {
        void setImageOnMainThread(Bitmap bitmap, T holder, String url);
    }

    public ImageDownloader(Handler mainHandler, CallbackDownloadImage callbackDownloadImage) {
        super(name);
        this.mainHandler = mainHandler;
        this.mCallbackDownloadImage = callbackDownloadImage;
    }


    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        imageDownloadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == WHAT){
                    T holder = (T) msg.obj;
                    handleRequest(holder);
                }
            }
        };
    }

    private void handleRequest(final T holder) {
        try {
            final String url = urlAdapterListMap.get(holder);
            if(url == null){
                return;
            }
            byte[] bitmapBytes = getBitmapBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(!urlAdapterListMap.get(holder).equals(url) && hasQuit){
                        return;
                    }
                    urlAdapterListMap.remove(holder);
                    mCallbackDownloadImage.setImageOnMainThread(bitmap, holder, url);
                }
            });
        }catch (IOException exc){
            exc.printStackTrace();
        }


    }

    private byte[] getBitmapBytes(String url) throws IOException {

            URL urlConnecction = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnecction.openConnection();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream inputStream = httpURLConnection.getInputStream();
            if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException("Response code" + httpURLConnection.getResponseMessage());
            }
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1){
                baos.write(buffer, 0, bytesRead);
            }
            baos.close();
            return baos.toByteArray();
        }finally {
            httpURLConnection.disconnect();
        }

    }

    @Override
    public boolean quit() {
        hasQuit = true;
        return super.quit();

    }

    public void downloadImage(T holder, String url){
        if(url == null){
            urlAdapterListMap.remove(holder);
        }else {
            urlAdapterListMap.put(holder, url);
            imageDownloadHandler.obtainMessage(WHAT, holder).sendToTarget();
        }
    }

    void clear(){
        imageDownloadHandler.removeMessages(WHAT);
        urlAdapterListMap.clear();
    }
}
