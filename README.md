## 一、服务器端

先打开手机服务器，使客户端在同一ip下即可完成wifi热点下通信，服务器端是用Socket 实现，Socket基础可参考我的上一篇博文《[手机服务器微架构设计与实现 之 http server](https://github.com/li-fengjie/SimpleHttpServer)》

服务器线程类不断读取客户端数据，程序使用readFromClient()方法来读取客户数据,如果在读取数据过程中捕获到 Ioexception异常,则表明该 Socket对应的客户端 Socket出现了问题(到底什么问题我们不管,反正不正常),程序就将该 Socket从 socketlist中删除。当服务器线程读到客户端数据之后,程序遍历 socketlist集合,并将该数据向 socketlist集合中的每个 Socket发送一次该服务器线程将把从 Socket中读到的数据向 socketlist中的每个 Socket转发一次。

**应用效果图:**

<img src="https://images2017.cnblogs.com/blog/1068222/201801/1068222-20180101204506862-1085498701.png" style="zoom:40%" />

​										      **服务器端**

### 所需权限：

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
```

## 二、客户端 ##

**应用效果图:**

<img src="https://images2017.cnblogs.com/blog/1068222/201801/1068222-20180101203940799-626447183.png" style="zoom:40%" />
​										      **客户端**

​	当用户单击该程序界面中的“发送”按钮后,程序将会把nput输入框中的内容发送给clientthread的 rehandle对象, clientThread负责将用户输入的内容发送给服务器。为了避免UI线程被阻塞,该程序将建立网络连接、与网络服务器通信等工作都交给Client thread线程完成。

​	Client thread子线程负责建立与远程服务器的连接,并负责与远程服务器通信,读到数据之后便通过 Handler对象发送一条消息;当 Client thread子线程收到UI线程发送过来的消息(消息携带了用户输入的内容)后,还负责将用户输入的内容发送给远程服务器。该子线程的代码如上ClientThread代码。

​	ClientThread线程的功能是不断的获取Soket输出流中的内容，当读到Socket输入流中内容后，便通过Handler对象发送一条消息，消息负责携带读到的数据。该线程还负责读取UI线程发送的消息，接收消息后，该子线程负责将消息中携带的数据发送到远程服务器。

### ClientThread子线程中Looper学习：

![img](https://images2017.cnblogs.com/blog/1068222/201801/1068222-20180101212641831-414667134.png)

传送门：

[客户端](https://github.com/li-fengjie/MultiThreadClient)

[服务器端](https://github.com/li-fengjie/ServerDemo)