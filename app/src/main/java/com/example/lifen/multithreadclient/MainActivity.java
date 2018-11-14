package com.example.lifen.multithreadclient;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText input;
    private Button send;
    private TextView show;
    Handler handler;
    private ClientThread clientThread;
    private TextView ip;
    public static String ipAddress;
    public static String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mac = getMacAddress();
        input = (EditText) findViewById(R.id.input);
        send = (Button) findViewById(R.id.send);
        show = (TextView) findViewById(R.id.show);
        ip = (TextView) findViewById(R.id.ip);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x123){
                    show.setTextColor(Color.RED);
                    show.append("\n" + msg.obj.toString());
                }
            }
        };
        clientThread = new ClientThread(handler);
        new Thread(clientThread).start();

        ipAddress = getLocalIpAddress();

        ip.setText("当前IP: "+ipAddress + "-----本机Mac：" + getMacAddress());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText().toString().equals("")){
                    return;
                }
                try{
                    Message msg = new Message();
                    msg.what = 0x345;
                    msg.obj = input.getText().toString();
//                    Log.i(TAG, "onClick: "+msg.obj);
                    clientThread.revHandler.sendMessage(msg);
                    input.setText("");
                }catch (Exception e) {
                    Log.e(TAG, "onClick: ", e);
                }
            }
        });
    }

    public String getLocalIpAddress() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni: nilist)
            {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address: ialist){
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress())
                    {
                        ipv4=address.getHostAddress();
                        return ipv4;
                    }
                }
            }

        } catch (SocketException ex) {
            Log.e("localip", ex.toString());
        }
        return null;
    }

    public static String getMacAddress(){
 /*获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。*/
        //        String macAddress= "";
//        WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        macAddress = wifiInfo.getMacAddress();
//        return macAddress;

        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }
}
