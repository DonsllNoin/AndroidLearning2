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
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.androidlearning2.MainActivity;
import com.example.androidlearning2.R;

public class MyService extends Service {

    private static final String TAG = "MyService";

    private DownloadBinder mBinder = new DownloadBinder();

    public static class DownloadBinder extends Binder{

        public void startDownload(){
            Log.d(TAG, "startDownload: executed");

        }

        public int getProgress(){
            Log.d(TAG, "getProgress: executed");
            return 0;

        }
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // 服务 创建 的时候调用
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: executed");

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
        Notification notification = new Notification.Builder(this, id)
                .setContentTitle("This is Content title")
                .setContentText("This is content text")
                // 获取 系统 时间
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                // 设置跳转
                .setContentIntent(pi)
                .build();

        // 调用了这个方法之后，就会让 Service 变成一个前台服务，在状态栏中显示出来
        startForeground(1, notification);



    }

    // 服务 启动 的时候调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 具体处理的逻辑


                // 防止该服务一直处于运行的状态，要设置当其完成任务后要结束掉自己
                stopSelf();
            }
        }).start();

        Log.d(TAG, "onStartCommand: executed");
        return super.onStartCommand(intent, flags, startId);
    }

    // 服务 摧毁 的时候调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: executed");
    }

}