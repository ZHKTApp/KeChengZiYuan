package com.zwyl.course.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadAsyncTask extends AsyncTask<String, Integer, String> {
    private String mUrl;
    private Context mContext;
    private String resourceName;
    private OnProgressListener onProgressListener;
    public DownloadAsyncTask(String mUrl, String resourceName, Context context,OnProgressListener onProgressListener) {
        this.mUrl = mUrl;
        this.resourceName = resourceName;
        mContext = context;
        this.onProgressListener = onProgressListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (mUrl == null) {
            return null;
        }
        InputStream in = null;
        BufferedOutputStream out = null;
        OutputStream fileOutputStream = null;
        onProgressListener.onStart();
        try {
            URL url = new URL(mUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(url.openStream(), StreamUtils.IO_BUFFER_SIZE);
            String downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ys_download" + File.separator;
            File downFile = new File(downloadPath);
            File dir = new File(downFile, DeviceUtil.getPackageName(mContext));
            if (!dir.exists()) dir.mkdirs();
            String fileName = resourceName + mUrl.substring(mUrl.lastIndexOf("."), mUrl.length());
            File filePath = new File(dir, fileName);
            File[] files = dir.listFiles();
//            Log.e("http", "downFile  : " + downFile + " dir : " + dir + " filename : " + fileName + " filePath : " + filePath);
//            if (files != null && files.length != 0)
//                for (int i = 0; i < files.length; i++) {
//                    String absolutePath = files[i].getAbsolutePath();
//                    String[] splits = absolutePath.split("/");
//                    String name = splits[splits.length - 1];
//                    if (name.equals(fileName)) {
//                        Log.e("http", "fileDownload : 已经下载");
//                        return null;
//                    }
//                }
            fileOutputStream = new FileOutputStream(filePath, false);
            out = new BufferedOutputStream(fileOutputStream, StreamUtils.IO_BUFFER_SIZE);
            int contentLength = httpURLConnection.getContentLength();
            StreamUtils.coyp(in, out,onProgressListener,contentLength);
            out.flush();
            onProgressListener.onFinish();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onProgressListener.onError();
        } catch (IOException e) {
            e.printStackTrace();
            onProgressListener.onError();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public interface OnProgressListener{
         void onProgress(double progress);
         void onFinish();
         void onStart();
         void onError();
    }
}
