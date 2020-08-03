package com.hcrpurdue.jason.hcrhousepoints.Utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class ImageCacheManager {
    private static ImageCacheManager instance = null;
    private LruCache<String, Bitmap> memoryCache;

    public static ImageCacheManager getInstance() {
        if (instance == null) {
            synchronized (ImageCacheManager.class) {
                if (instance == null) {
                    instance = new ImageCacheManager();
                    return instance;
                }
            }
        }
        return instance;
    }

    ImageCacheManager(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (memoryCache.get(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public void setImageViewFromDownloadURL(String downloadURL, ImageView imageView) {
        if(memoryCache.get(downloadURL) != null){
             imageView.setImageBitmap(memoryCache.get(downloadURL));
        }
        else{
            ImageLoader imageLoader = new ImageLoader(new ImageLoaderInterface() {
                @Override
                public void hasBitmap(Bitmap bitmap) {
                    addBitmapToMemoryCache(downloadURL, bitmap);
                    imageView.setImageBitmap(bitmap);
                }
            });
            imageLoader.execute(downloadURL);
        }
    }
}

 class ImageLoader extends AsyncTask<String, Void, Bitmap> {

    ImageLoaderInterface imageLoaderInterface;

    ImageLoader(ImageLoaderInterface imageLoaderInterface){
        this.imageLoaderInterface = imageLoaderInterface;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Bitmap bitmap = null;
        try{
            InputStream inputStream = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageLoaderInterface.hasBitmap(bitmap);
    }
}

interface ImageLoaderInterface {

    default void hasBitmap(Bitmap bitmap){}
}

