package com.example.androidlearning2.Service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyIntentService extends IntentService {

    private static final String TAG = "MyIntentService";

    // 构造函数要这么写，一个无参的构造函数
    public MyIntentService() {
        // 调用父类的构造函数
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 打印当前线程的 id
        Log.d(TAG, "Thread id is: " + Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: executed");
    }
}
