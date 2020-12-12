package com.mycp.updatelib;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Description:
 * Data：2018/11/5-18:55
 * Author: Allen
 */
public class UpdateManager {
    Context context;
    //apk下载地址
    String apkUrl;
    //apk存储路径目录，只能是当前应用私有目录
    String apkDirName;
    //apk名称
    String apkName;
    //更新日志
    String updateLog = "";
    //标题
    String title = "";
    //副标题
    String subTitle = "";
    //是否强制更新
    boolean isForce;
    //是否点击周围空白取消
    boolean isOutside = false;
    //是否显示进度条
    boolean isProgress;

    public UpdateManager(Context context) {
        this.context = context;
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        UpdateManager mUpdateManager;

        public Builder(Context context) {
            mUpdateManager = new UpdateManager(context);
        }

        /**
         * description: 设置apk下载路径
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setApkUrl(String apkUrl) {
            mUpdateManager.apkUrl = apkUrl;
            return this;
        }

        /**
         * description: 设置apk下载路径的目录名，在当前私有目录下，不设置就使用默认路径
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setApkDirName(String apkDir) {
            mUpdateManager.apkDirName = apkDir;
            return this;
        }

        /**
         * description: 设置apk名称，不设置默认截取最后一个/之后的名称
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setApkName(String apkName) {
            mUpdateManager.apkName = apkName;
            return this;
        }

        /**
         * description: 设置更新日志
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setUpdateLog(String updateLog) {
            mUpdateManager.updateLog = updateLog;
            return this;
        }

        /**
         * description: 设置标题
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setTitle(String title) {
            mUpdateManager.title = title;
            return this;
        }

        /**
         * description: 副标题设置
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setSubTitle(String subTitle) {
            mUpdateManager.subTitle = subTitle;
            return this;
        }

        /**
         * description: 是否强制更新，isForce默认是false
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setForceUpdate(boolean isForce) {
            mUpdateManager.isForce = isForce;
            return this;
        }

        /**
         * description: 是否点击空白取消，isOutside默认是false
         * author: Allen
         * date: 2018/11/22 15:52
         */
        public Builder setOutside(boolean isOutside) {
            mUpdateManager.isOutside = isOutside;
            return this;
        }

        /**
         * description: 点击确定按钮之后，是否显示进度条，默认是显示的
         * author: Allen
         * date: 2018/11/22 15:51
         */
        public Builder setShowProgress(boolean isProgress) {
            mUpdateManager.isProgress = isProgress;
            return this;
        }

        public UpdateManager build() {
            return mUpdateManager;
        }
    }

    String apkFilePath = null;
    //是否已经下载没有安装
    boolean isYetDownloadNoInstall = false;

    public void showUpdate() {
        String apkDirPath ="";
        if (TextUtils.isEmpty(apkDirName)) {
            //直接使用默认路径和名称
            apkDirPath = context.getExternalFilesDir(UpdateUtils.apkDir).getAbsolutePath();
        } else {
            apkDirPath = context.getExternalFilesDir(apkDirName).getAbsolutePath();
        }

        if (TextUtils.isEmpty(apkName)) {
            apkFilePath = apkDirPath + File.separator + UpdateUtils.getNameFromUrl(apkUrl);
        } else {
            apkFilePath = apkDirPath + File.separator + apkName;
        }
        File file = new File(apkFilePath);
        if (file.exists()) {
            //已经存在，直接安装
            isYetDownloadNoInstall = true;
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //context需定义
        View view = layoutInflater.inflate(R.layout.def_update_dialog, null);
        final Dialog dialog = UpdateUtils.getCenterDialog(context, view);

        TextView title_tv = view.findViewById(R.id.title);
        TextView subtitle_tv = view.findViewById(R.id.subtitle);
        TextView confirm = view.findViewById(R.id.confirm);
        TextView cancel = view.findViewById(R.id.cancle);
        TextView content = view.findViewById(R.id.content);
        final LinearLayout button_layout = view.findViewById(R.id.button_layout);
        final ProgressBar progressBar = view.findViewById(R.id.progress);
//        progressBar.setProgressTintList(valueOf(RED));
//        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.common_pressed), PorterDuff.Mode.SRC_IN);

        final TextView install = view.findViewById(R.id.install);
        if (isForce) {
            //如果是强制更新，就设置对话框不可取消
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        } else {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
        }
        if (!TextUtils.isEmpty(title)) {
            title_tv.setText(title);
        }
        if (!TextUtils.isEmpty(subTitle)) {
            subtitle_tv.setText(subTitle);
        } else {
            subtitle_tv.setVisibility(View.GONE);
        }
        progressBar.setMax(100);
        content.setText(updateLog);

        if (isYetDownloadNoInstall) {
            button_layout.setVisibility(View.GONE);
            install.setVisibility(View.VISIBLE);
        }
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //直接去安装
                installApk(context, apkFilePath);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProgress) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                if (isForce) {
                    progressBar.setVisibility(View.VISIBLE);
                    button_layout.setVisibility(View.GONE);
                } else {
                    if (isProgress) {
                        progressBar.setVisibility(View.VISIBLE);
                        button_layout.setVisibility(View.GONE);
                    } else {
                        dialog.dismiss();
                    }
                }
                //立即更新
                DownloadUtil.getInstance().download(context, apkUrl, apkName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(String path) {
                        //去安装
                        installApk(context, path);
                        install.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onDownloading(int progress) {
                        Log.e("下载进度>>>>", Thread.currentThread().getName() + "进度" + progress);
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onDownloadFailed() {
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //暂不更新
                if (!isForce) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    /**
     * 安装apk文件
     */
    public static void installApk(Context context, String apkPath) {
        File file = new File(apkPath);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, UpdateUtils.getPackageName(context) + ".fileprovider", file);
                //应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

}
