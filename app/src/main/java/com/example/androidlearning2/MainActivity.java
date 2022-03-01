package com.example.androidlearning2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.androidlearning2.Service.MyIntentService;
import com.example.androidlearning2.Service.MyService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private MyService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 利用 Binder 作为连接 活动 与 服务 的纽带
            // 通过这个 binder 就能调用 Service 中的方法了
            downloadBinder = (MyService.DownloadBinder) service;
            downloadBinder.startDownload();
            downloadBinder.getProgress();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 服务 的启动
        Button startService = findViewById(R.id.start_service);
        Button stopService = findViewById(R.id.stop_service);

        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);

        // 活动 与 服务 的联系
        Button bindService = findViewById(R.id.bind_service);
        Button unbindService = findViewById(R.id.unbind_service);

        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);

        // 创建一个 标准服务 对象
        Button startIntentService = findViewById(R.id.start_intent_service);

        startIntentService.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.start_service:
                Intent startIntent = new Intent(this, MyService.class);
                // 启动服务
                startService(startIntent);

                break;

            case R.id.stop_service:
                Intent stopIntent = new Intent(this, MyService.class);
                // 停止服务
                stopService(stopIntent);
                break;

            case R.id.bind_service:
                Intent bindIntent = new Intent(this, MyService.class);
                // 绑定服务
                bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;

            case R.id.unbind_service:
                // 解绑服务
                unbindService(connection);
                break;

            case R.id.start_intent_service:
                // 打印主线程的 id
                Log.d(TAG, "Thread id is: " + Thread.currentThread().getId());

                // 启动服务，意思就是调用了另一个类来创建子线程，来完成任务
                Intent intentService = new Intent(this, MyIntentService.class);
                startService(intentService);
                break;

            default:
                break;

        }

    }
}