package com.example.androidlearning2.Service;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 *          String : 表示需要传入一个 字符串参数 给 后台任务
 *
 *          Integer : 使用 整型数据 来作为 进度 的显示单位
 *
 *          Integer : 使用 整型数据 来 反馈执行结果
 *
 * */
public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    // 待会会将下载结果通过 DownloadListener 进行回调
    public DownloadTask(DownloadListener listener){
        this.listener = listener;
    }

    @Override
    // String... 表示可以传入一个或多个 String 变量，这里传入的是 下载地址
    // 用于 后台执行 具体的 下载逻辑
    protected Integer doInBackground(String... params) {

        InputStream is = null;
        RandomAccessFile saveFile = null;
        File file = null;

        try{
            // 记录已下载的文件的大小
            long downloadedLength = 0;

            // 得到了 下载地址
            String downloadUrl = params[0];

            // 解析出了 下载文件名
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));

            // 指定文件下载到 Environment.DIRECTORY_DOWNLOADS 目录下
            String directory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            if (file.exists()){
                downloadedLength = file.length();
            }
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0){
                return TYPE_FAILED;
            } else if(contentLength == downloadedLength){
                // 一下载字节和文件总字节相等，说明文件已经下载完成
                return TYPE_SUCCESS;
            }


            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    // 断点下载，指定从哪个字节开始下载
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();

            Response response = client.newCall(request).execute();
            if (response != null){
                is = response.body().byteStream();
                saveFile = new RandomAccessFile(file, "rw");

                // 跳过已下载的字节
                saveFile.seek(downloadedLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;

                while((len = is.read(b)) != -1){

                    if (isCanceled){
                        return TYPE_CANCELED;

                    }else if (isPaused){
                        return TYPE_PAUSED;

                    }else {
                        total += len;
                        saveFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int)((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);

                    }
                }

                response.body().close();
                return TYPE_SUCCESS;
            }

        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            try{
                if (is != null){
                    is.close();
                }
                if (saveFile != null){
                    saveFile.close();
                }
                if (isCanceled && file != null){
                    file.delete();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return TYPE_FAILED;
    }

    @Override
    // 在界面上更新当前的 下载进度
    protected void onProgressUpdate(Integer... values) {

        int progress = values[0];
        if (progress > lastProgress){

            // 通知下载进度的更新
            listener.onProgress(progress);
            lastProgress = progress;
        }

    }

    @Override
    // 通知最终的 下载结果
    protected void onPostExecute(Integer status) {

        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;

            case TYPE_FAILED:
                listener.onFailed();
                break;

            case TYPE_PAUSED:
                listener.onPaused();
                break;

            case TYPE_CANCELED:
                listener.onCanceled();
                break;

            default:
                break;

        }
    }

    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();

        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;

    }

}
