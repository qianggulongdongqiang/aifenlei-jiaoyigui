package com.arcfun.uhfclient.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.arcfun.uhfclient.data.GridAdapter.ViewHolder;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private File cache;

    public ImageLoader() {
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        LogUtils.d(TAG, "cache=" + Environment.getExternalStorageDirectory());
        if (!cache.exists()) {
            cache.mkdirs();
        }
    }
    /**
     * 加载图片，缓存中有就从缓存中拿，没有就下载
     * @param url
     * @param adapter
     * @param holder
     */
    public void loadImage(String url, BaseAdapter adapter, ViewHolder holder) {
        try {
            String name = Utils.getMD5(url) + url.substring(url.lastIndexOf("."));
            File file = new File(cache, name);
            LogUtils.d(TAG, "loadImage=[" + name + "], " + file.exists());
            if (file.exists()) {
                holder.iv_icon.setImageURI(Uri.fromFile(file));
            } else {
                ImageLoaderTask task = new ImageLoaderTask(holder);
                task.execute(url, adapter, holder);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "loadImage", e);
        }
    }

    public void loadImage(String url, ImageView view) {
        String name = Utils.getMD5(url) + url.substring(url.lastIndexOf("."));
        File file = new File(cache, name);
        LogUtils.d(TAG, "loadImage=[" + name + "], " + file.exists());
        if (file.exists()) {
            view.setImageURI(Uri.fromFile(file));
        } else {
            SlideListLoaderTask task = new SlideListLoaderTask(view);
            task.execute(url);
        }
    }

    private class ImageLoaderTask extends AsyncTask<Object, Void, Uri> {
        String url;
        BaseAdapter adapter;
        ViewHolder holder;

        private ImageLoaderTask(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected Uri doInBackground(Object... params) {
            url = (String) params[0];
            adapter = (BaseAdapter) params[1];
            String name = Utils.getMD5(url) + url.substring(url.lastIndexOf("."));
            return getImageUri(url, new File(cache, name));
        }

        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);
            if (result == null) {
                return;
            }
            holder.iv_icon.setImageURI(result);
            adapter.notifyDataSetChanged();
        }
    }

    private class SlideListLoaderTask extends AsyncTask<Object, Void, Uri> {
        String url;
        ImageView view;

        private SlideListLoaderTask(ImageView view) {
            this.view = view;
        }

        @Override
        protected Uri doInBackground(Object... params) {
            url = (String) params[0];
            String name = Utils.getMD5(url) + url.substring(url.lastIndexOf("."));
            return getImageUri(url, new File(cache, name));
        }

        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);
            if (result == null) {
                return;
            }
            view.setImageURI(result);
        }
    }

    private static Uri getImageUri(String path, File file) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            if (200 == connection.getResponseCode()) {
                InputStream is = connection.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte [] buffer = new byte[2048];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0 ,len);
                }
                is.close();
                fos.close();
                return Uri.fromFile(file);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.toString());
        }
        return null;
    }
}