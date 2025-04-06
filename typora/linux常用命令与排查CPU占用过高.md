# linux常用命令与排查CPU占用过高

#### 1.常用Linux命令

![](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123172738318.png)

![image-20241123173026334](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123173026334.png)

![image-20241123173343522](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123173343522.png)

![image-20241123173440325](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123173440325.png)

```shell
1.top/uptime
2.vmstat -n 2 3 每两秒查看一次，共三次
free   free -g  free -m  是转成g,该是兆
df   df -h(human)
iostat -sdk 2 3
ifstat -1
```

![image-20241123173514450](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123173514450.png)

![image-20241123173551609](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123173551609.png)

#### 2.排查CPU过高

![image-20241123171650899](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123171650899.png)

![image-20241123171919778](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123171919778.png)

![image-20241123172058400](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241123172058400.png)

```shell
1.top
2.ps -ef|grep java|grep -v grep 或者使用jsp命令查看
3.定位该程序的具体哪些线程占比高：ps -mp 进程号 -o THREAD,tid,time
4.将线程ID转成16进制
5.jstack 进程ID | grep 线程tid(16进制小写英文) -A60 (打印前60行)
最后就成确定哪一行出现问题了
```

