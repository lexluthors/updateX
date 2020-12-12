package com.mycp.updatelib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Description:
 * Data：2018/11/2-16:46
 * Author: Allen
 */
public class DownloadUtil {
    public static final int DOWNLOAD_FAIL = 0;
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_SUCCESS = 2;
    private static  DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil getInstance() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    Message message;

    /**
     * @param url      下载地址
     * @param fileName 保存的文件名称
     * @param listener 监听器
     */
    public void download(final Context context,final String url, final String fileName, final OnDownloadListener listener) {
        this.listener = listener;
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is = null;
                FileOutputStream fos = null;
                        try {
                            byte[] buf = new byte[4096];
                            //储存下载文件的目录
                            String apkFilePath = context.getExternalFilesDir("apk").getAbsolutePath();
                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            File file = null;
                            if (TextUtils.isEmpty(fileName)) {
                                file = new File(apkFilePath + File.separator +  UpdateUtils.getNameFromUrl(url));
                            } else {
                                file = new File(apkFilePath + File.separator + fileName);
                            }
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            int len = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                //下载中
                                message = mHandler.obtainMessage();
                                message.what = DOWNLOAD_PROGRESS;
                                message.obj = progress;
                                message.sendToTarget();
//                                mHandler.sendMessage(message);
                                Log.e("哈哈哈", "这里走了吗sum="+sum+"--progress--"+progress+"--len--"+len);
                            }
                            is.close();
                            fos.flush();
                            //下载完成
                            message = Message.obtain();
                            message.what = DOWNLOAD_SUCCESS;
                            message.obj = file.getAbsolutePath();
                            mHandler.sendMessage(message);
                        } catch (Exception e) {
                            Message message = Message.obtain();
                            message.what = DOWNLOAD_FAIL;
                            mHandler.sendMessage(message);
                        } finally {
                            if (is != null){
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = DOWNLOAD_FAIL;
                mHandler.sendMessage(message);
            }
        });
    }

    private String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_PROGRESS:
                    listener.onDownloading((Integer) msg.obj);
                    break;
                case DOWNLOAD_FAIL:
                    listener.onDownloadFailed();
                    break;
                case DOWNLOAD_SUCCESS:
                    listener.onDownloadSuccess((String) msg.obj);
                    break;
            }
        }
    };


    OnDownloadListener listener;

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(String path);

        /**
         * 下载进度
         *
         * @param progress
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

}
