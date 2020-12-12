package com.mycp.updatex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

/**
 * Description:
 * Data：20-6-11-下午12:56
 * Author: Allen
 */
public class FileUtil {

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MYCP/";

    public static void createDir() {
        File f = new File(PATH);
        if(!f.exists()){
            f.mkdirs();
        }
    }

    public static String getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return "0 MB";
        } else {
            DecimalFormat df = new DecimalFormat("#0.00");
            long size = f.length();
            float s = (size / 1024f) / 1024f;
            return df.format(s) + "MB";
        }
    }

    public static void scanFile(Context context, String path) {
        // 广播通知扫描文件，可以在图库中显示
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

//    public static VideoInfoBean getVideoInfo(String mVideoPath) {
//        if(!AssertUtils.INSTANCE.isVideo(mVideoPath)){
//            return new VideoInfoBean(0, 0, 0, 0);
//        }
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(mVideoPath);
//        LogUtils.loge("去你妈的》>》>》" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//        int videoTime = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//获取视频时长
//        int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));//获取视频的宽度
//        int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));//获取视频的高度
//        int videoRotate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));//获取视频的角度
//        LogUtils.loge("角度是多少>>>>>>"+videoRotate);
//        if (videoRotate == 90||videoRotate==270) {
//            int w = videoWidth;
//            videoWidth = videoHeight;
//            videoHeight = w;
//        }
//        return new VideoInfoBean(videoTime, videoWidth, videoHeight, videoRotate);
//    }

    //可能会报错,异常
    public static int getMusicDuration(String musicPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(musicPath);
        return Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    public static String getSuffix(String path) {
        int index = path.lastIndexOf(".");
        if (index > -1) {
            return path.substring(index + 1);
        }
        return path;
    }

    public static int[] getImageWH(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//这个参数设置为true才有效，
        Bitmap bmp = BitmapFactory.decodeFile(path, options);//这里的bitmap是个空
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        return new int[]{outWidth, outHeight};
    }



    public static void openFile(Context context, String filePath, int type) {
        Intent shareIntent = new Intent();
        //1调用系统分析
        shareIntent.setAction(Intent.ACTION_VIEW);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        switch (type) {
            case 1:
                shareIntent.setType("video/*");
                break;
            case 2:
                shareIntent.setType("image/*");
                break;
            case 3:
                shareIntent.setType("audio/*");
                break;
        }
        context.startActivity(Intent.createChooser(shareIntent, "分享"));
    }

    public static void shareImageFile(Context context, String path) {
        Intent shareIntent = new Intent();
        //1调用系统分析
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享"));
    }

    public static void shareVideoFile(Context context, String path) {
        Intent shareIntent = new Intent();
        //1调用系统分析
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        shareIntent.setType("video/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享"));
    }

    public static void shareAudioFile(Context context, String path) {
        Intent shareIntent = new Intent();
        //1调用系统分析
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        shareIntent.setType("audio/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享"));
    }


    public static void openFile(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //获取文件下载路径
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //调用系统文件管理器打开指定路径目录
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setDataAndType(Uri.fromFile(dir), "file/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(dir), "file/*");
        context.startActivity(intent);
    }

    public static void openPlayer(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "video/*");
        context.startActivity(intent);
    }

    public static void openMp3Player(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        context.startActivity(intent);
    }

    public static void openImage(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        context.startActivity(intent);
    }

    public static void shareText(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "share");
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(
                Intent.createChooser(intent, "share"));
    }

    /**
     * 没有文件夹。创建文件夹
     *
     * @param filePath
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                Boolean isTrue = file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    public static File writeVideoTXT(String content) {
        try {
            // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
            /* 写入Txt文件 */
            File writename = new File(PATH + "filelist.txt");// 相对路径，如果没有则要建立一个新的output。txt文件
            if (writename.exists()) {
                writename.delete();
            }
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(content); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
            return writename;
        } catch (Exception e) {
            e.printStackTrace();
            return new File(PATH + "filelist.txt");
        }
    }

    public static File writeGifTXT(String content) {
        try {
            // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
            /* 写入Txt文件 */
            File writename = new File(PATH + "filelist.txt");// 相对路径，如果没有则要建立一个新的output。txt文件
            if (writename.exists()) {
                writename.delete();
            }
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(content); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
            return writename;
        } catch (Exception e) {
            e.printStackTrace();
            return new File(PATH + "filelist.txt");
        }
    }


    public static String getNewFileName(String name, String path) {
        return (getFolderName(path) + File.separator + name + "." + getSuffix(path)).trim();
    }

    public static String getFileName(String path) {
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return path.substring(start + 1, end);
        } else {
            return "";
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 重命名文件
     * oldPath 和 newPath必须是新旧文件的绝对路径
     */
    public static boolean renameFile(String oldPath, String newPath) {
        File file = new File(oldPath);
        return file.exists() && file.renameTo(new File(newPath));
    }

    /**
     * 重命名文件\文件夹
     * @param filepath
     * @param newName
     * @return
     */
    public static boolean rename(String filepath, String newName) {
        File file = new File(filepath);
        return file.exists() && file.renameTo(new File(newName));
    }

    /**
     * 获取文件夹名称
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {
        if (filePath == null || filePath.length() == 0 || filePath.trim().length() == 0) {
            return filePath;
        }
        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }
}
