package com.example.androidlearning2.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.androidlearning2.MainActivity;
import com.example.androidlearning2.R;

import java.io.File;

// 保证 下载任务 在 后台程序 中持续运行
public class DownloadService extends Service {

    private DownloadTask downloadTask;

    private String downloadUrl;

    private DownloadListener listener = new DownloadListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onSuccess() {

            // 先清空下载任务
            downloadTask = null;

            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onFailed() {

            downloadTask = null;
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPaused() {

            downloadTask = null;
            Toast.makeText(DownloadService.this, "Paused", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCanceled() {

            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Paused", Toast.LENGTH_SHORT).show();


        }
    };

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;

    }

    class DownloadBinder extends Binder{

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void startDownload(String url){
            if (downloadTask == null){
                downloadUrl = url;
                // 将 下载状态 传入
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);

                // 打开 前台服务，显示正在下载
                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadService.this, "Downloading...", Toast.LENGTH_SHORT).show();

            }
        }

        public void pauseDownload(){
            if (downloadTask != null){
                // 暂停下载
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if (downloadTask != null){
                downloadTask.cancelDownload();
            }else {
                if (downloadUrl != null){
                    // 取消下载时需将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String direction = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(direction + fileName);
                    if (file.exists()){
                        file.delete();
                    }

                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    public NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification(String title, int progress){

        // 创建 通知管理助手
        NotificationManager manager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);

        // 创建 通知通道的id
        String id = "test";

        // 创建 通知通道的描述
        String name = "123";

        // 创建 是否弹出、是否有声音、是否有震动等等
        int importance = NotificationManager.IMPORTANCE_HIGH;

        // 创建 通知通道
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        manager.createNotificationChannel(mChannel);

        // 点击通知之后会进行跳转
        Intent[] intents = new Intent[]{new Intent(this, MainActivity.class)};
        PendingIntent pi = PendingIntent.getActivities(this, 0, intents, 0);

        // 创建 通知（要记得绑定 通知通道的 id）
        Notification.Builder builder = new Notification.Builder(this, id);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setContentIntent(pi);
            builder.setContentTitle(title);



        if (progress >= 0){
            // 当 progress 大于等于 0 时才需显示下载内容
            builder.setContentText(progress + "%");
            // 最大进度 、当前进度 、是否使用模糊进度条
            builder.setProgress(100, progress, false);

        }

        return builder.build();

    }


    public DownloadService() {
    }


}