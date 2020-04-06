package com.wei.ncue;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class UpdateApp {
    static String TAG = "UPDATEAPP";
    static String downloadUpdateApkFilePath;
    static String updateLog = "";

    public static void downloadBySelf(Context context, String apkUrl, String fileName, String updateLog) {
        if (TextUtils.isEmpty(apkUrl)) {
            return;
        }
        UpdateApp.updateLog = updateLog;
        try {
            Uri uri = Uri.parse(apkUrl);
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            //在通知欄中顯示
            request.setVisibleInDownloadsUi(true);
            request.setTitle("NCUE APP更新");
            request.setDescription(updateLog);
            //MIME_MapTable是所有文件的後綴名所對應的MIME類型的一個String數組  {".apk",    "application/vnd.android.package-archive"},
            request.setMimeType("application/vnd.android.package-archive");
            // 在通知欄通知下載中和下載完成
            // 下載完成後該Notification才會被顯示
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                // Android 3.0版本 以後才有該方法
                //在下載過程中通知欄會一直顯示該下載的Notification，在下載完成後該Notification會繼續顯示，直到用戶點擊該Notification或者消除該Notification
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }


            //downloadUpdateApkFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + fileName + System.currentTimeMillis() + ".apk";
            // Choose a directory using the system's file picker.
            /*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

            // Provide read access to files and sub-directories in the user-selected
            // directory.
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, downloadUpdateApkFilePath);

            new Activity().startActivityForResult(intent, 3);*/
            // 若存在，則刪除 (這裏具體邏輯具體看,我這裏是刪除)
            //context.deleteFile(downloadUpdateApkFilePath);
            //context.getExternalFilesDir(null)+File.separator+fileName +"ncue_by_wei.apk";
            downloadUpdateApkFilePath = context.getExternalFilesDir(null) + File.separator + fileName /*+ System.currentTimeMillis()*/ + ".apk";
            Uri fileUri = Uri.fromFile(new File(downloadUpdateApkFilePath));
            request.setDestinationUri(fileUri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

            //下載管理Id
            downloadManager.enqueue(request);
            DownloadReceiver mDownloaderReceiver = new DownloadReceiver();
            //註冊下載完成廣播
            context.registerReceiver(mDownloaderReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        } catch (Exception e) {
            e.printStackTrace();
            //註意:如果文件下載失敗則 使用瀏覽器下載
            downloadByWeb(context, apkUrl);
        }
    }

    private static void downloadByWeb(Context context, String apkUrl) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(apkUrl));
        context.startActivity(intent);
    }

    /**
     * 下載完成的廣播
     */
    public static class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (!TextUtils.isEmpty(downloadUpdateApkFilePath)) {
                SharedPreferences prefUpdate = context.getSharedPreferences("Update", MODE_PRIVATE);
                prefUpdate.edit()
                        .putBoolean("downloaded", true)
                        .putString("path", downloadUpdateApkFilePath)
                        .apply();

                installNormal(context, downloadUpdateApkFilePath);

            }
        }
    }

    /**
     * 提示安裝
     *
     * @param context 上下文
     * @param apkPath apk下載完成在手機中的路徑
     */
    public static void installNormal(final Context context, final String apkPath) {

        SharedPreferences prefUpdate = context.getSharedPreferences("Update", MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("NCUE APP更新")
                .setMessage(prefUpdate.getString("log", ""))
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        //installNormal(context, downloadUpdateApkFilePath);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //版本在7.0以上是不能直接通過uri訪問的
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                            File file = (new File(apkPath));
                            // 由於沒有在Activity環境下啟動Activity,設置下面的標簽
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //參數1:上下文, 參數2:Provider主機地址 和配置文件中保持一致,參數3:共享的文件
                            Log.d("apkPath", file.getAbsolutePath());
                            Uri apkUri = FileProvider.getUriForFile(context, "com.wei.ncue.fileprovider", file);
                            //添加這一句表示對目標應用臨時授權該Uri所代表的文件
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                                    "application/vnd.android.package-archive");
                        }
                        context.startActivity(intent);
                        Log.d(TAG, "successful?");

                    }
                });

        builder.create()
                .show();

    }
}
