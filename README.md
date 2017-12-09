# 版本说明

2017年12月3日：增加通过温度检测糖尿病足的功能

用R=R0（1+3.90802×10^-3T）就可以转换成温度了
我通过两个字节求出R，然后R0=100，带到那个公式里，求出T就行了是吧

## 2017年12月10日

- 测试一下*100求压力值的方法是不是正确的
- 原来的方法和二次方程的方法结果能不能对上

------

增加了长短脚的检测功能。

# 蓝牙的连接过程

**handler的应用**

两个activity：

- 接收处理蓝牙数据屏显示波形的activity：简称a2
- 搜索蓝牙设备的activity：简称a1

一个蓝牙处理对象：

BluetoothCommService：简称b1

需要注意b1的各种状态

## 具体的执行过程：

显示接收到的蓝牙数据的Activity在启动的时候，会创建一个处理蓝牙状态的类的对象，这个对象绑定了一个handler，当这个对象处理到一定的时间之后，就会用handler发送msg，handler会在主线程中处理该msg。

此时用户会点击菜单中的搜索蓝牙设备的菜单，此时会弹出一个搜索蓝牙设备的activity，即a2，用户搜索到蓝牙设备，并点击某个蓝牙设备之后，返回a1，此时a1会通过onActivityResult接收到a2返回的结果，如果返回了用户想要连接的蓝牙设备的名称，会通过b1的`connect`方法去连接该蓝牙设备。

同时a1中通过onResume方法去调用了b1的start方法，但是有条件，具体如下，需要好好研究一下。

**onResume方法通过代码看，应该是在onActivityResul方法执行之前执行的。所以需要判断一下`mCommService.getState() == BluetoothCommService.STATE_NONE`**

```$xslt
if (mCommService.getState() == BluetoothCommService.STATE_NONE) {
    // Start the Bluetooth services，开启监听线程
    mCommService.start();
}
```

蓝牙设备的连接过程：

1. connecting过程

b1会启动一个单独的线程去连接该设备，并发送msg给a1中的handler，告诉a1现在的连接状态是connecting。b1中的mState现在是`STATE_CONNECTING`。

单独的连接线程的处理是这样的，获取到device对应的socket，然后进行连接。连接成功之后，进入下一个状态的处理过程。

2. connected过程

进入connected状态之后，就可以从socket中获取流，也就是真正拿到数据。b1还是会启动一个单独的线程去处理connected，然后发送msg告诉a1，现在是connected状态，`mState`现在是`STATE_CONNECTED`。

单独的线程的处理过程是这样的，从socket获得流，然后读取字节，然后封装成msg发送给handler。

# 左右脚的结果计算

连接两个蓝牙的handler，获取数据之后，分别发送给各自的handler，分别是h1和h2。

h1接收到一组数据，也就是共8个传感器的数据，将其中四个加和当作一个传感器的数据s1，另外四个加和当作第二个传感器的数据s2，h1的作用就是获取这两个结果，放到他的一个队列q1中。

h2作用同上，接收到两个结果，分别为s3和s4，放到他的一个队列q2中。

如何将这个四个结果发送给另外一个handler h3来进行处理。

h1和h2每次接收到数据之后，都会出发一次h3，h3会检查两个队列中元素的多少，去少者的个数，获取到若干组数据，进行后续的处理。



