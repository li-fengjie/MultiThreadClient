package com.example.lifen.multithreadclient;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by LiFen on 2017/12/31.
 */

public class ClientThread implements Runnable {
    private static final String TAG = "ClientThread";
    private Socket s;
    //定义向UI线程发送消息的Handler 对象
    private Handler handler;
    //定义接收UI线程的消息 Handler对象
    public Handler revHandler;
    private BufferedReader br = null;
    private OutputStream os = null;

    public ClientThread(Handler handler){
        Log.d(TAG, "ClientThread() called with: handler = [" + handler + "]");
        this.handler = handler;
    }

    @Override
    public void run() {
        Log.d(TAG, "run() called");
        try{
            String iptemp = MainActivity.ipAddress;
            s = new Socket(iptemp,30000);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = s.getOutputStream();
            //启动一条子线程来读取服务器响应的数据
            new Thread(){
                @Override
                public void run() {
                    String content = null;
                    try {
                        while((content = br.readLine()) != null){
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = content;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            //为当前线程 初始化Looper
            Looper.prepare();
            //创建revHandler对象
            revHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == 0x345){
                        //将用户在文本框内输入网络
                        try{
                            os.write((MainActivity.mac.substring(9) + msg.obj.toString() + "\r\n").getBytes("utf-8"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
//            启动Looper
            Looper.loop();
        }catch (SocketTimeoutException e1){
            Log.i(TAG, "run: 网络连接超时");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
