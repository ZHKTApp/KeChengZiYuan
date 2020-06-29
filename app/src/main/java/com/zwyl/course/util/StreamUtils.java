package com.zwyl.course.util;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static final int IO_BUFFER_SIZE = 1024;

    public static void coyp(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    public static void coyp(InputStream in, OutputStream out, DownloadAsyncTask.OnProgressListener onProgressListener,int length) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        int avaiable = length;
        int current = 0;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
            current +=read;
            Log.i("gxh",current+"   "+avaiable);
            onProgressListener.onProgress((double)current / avaiable * 100);
        }
    }
}
