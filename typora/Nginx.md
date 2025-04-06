# Nginx



### Nginx的安装

```shell
sudo apt-get install nginx
```





## 1. 反向代理

* 客户端通过访问Nginx，然后Nginx在将请求转发到服务端，实现服务端ip地址的隐藏

```shell
# 将访问Nginx端带有/api 的路径都转发到服务端
server {
	listen 80;
	server_name ubuntuip;
	location ^~ /api {
		proxy_pass http://192.169.3.9:8080;
	}
}
```

* localtion 路径匹配的四种方式

  ``` shell
  location [= | ~ | ~* | ^~] url {
  
  }
  ```

  

  * =  不含有正则表达式，严格匹配
  * ~  包含正则表达式，区分大小写
  * ~* 包含正则表达式，不区分大小写
  * ^~ 不包含正则表达式，匹配度最高的url 

## 2. 负载均衡

* 写法1(默认轮询)

  ``` shell
  upstream myservername {
  	server 192.168.3.9:8080;
  	server 192.168.3.9:8089;
  }
  server {
  	listen 80;
  	server_name ubuntuip;
  	location ^~ /api {
  		proxy_pass http://myservername;
  	}
  }
  ```

* 权重
  ```shell
  upstream myservername {
  	server 192.168.3.9:8080 weight=10;
  	server 192.168.3.9:8089 weight=11;
  }
  server {
  	listen 80;
  	server_name ubuntuip;
  	location ^~ /api {
  		proxy_pass http://myservername;
  	}
  }
  ```

  

* hash,可以解决session共享问题
  ```shell
  upstream myservername {
  	ip_hash;
  	server 192.168.3.9:8080;
  	server 192.168.3.9:8089;
  }
  server {
  	listen 80;
  	server_name ubuntuip;
  	location ^~ /api {
  		proxy_pass http://myservername;
  	}
  }
  ```

  

* fair，根据服务器响应时间长短策略
  ```shell
  upstream myservername {
  	server 192.168.3.9:8080;
  	server 192.168.3.9:8089;
  	fair;
  }
  server {
  	listen 80;
  	server_name ubuntuip;
  	location ^~ /api {
  		proxy_pass http://myservername;
  	}
  }
  ```

## 3. 动静分离

```shell
server {
    listen 80;
    server_name example.com;
    root /var/www/static;#可以写在server中作为全局虚拟机的根目录
    index index.html index.htm;#全局index

    location / {
        try_files $uri $uri/ @application;# application 可以随便起名字
            root /var/www/static;#也可以卸载location中，覆盖server的root
    		index index.html index.htm;#location中的index
    		autoindex on;#打印出目录
    }

    location @application {
    	rewrite ^/(.*)$ /$1 break;#地址重定向
        proxy_pass http://127.0.0.1:8000;
    }
}
#注意上方是先去静态文件夹寻找目标，如果找不到将请求转发到服务器
```

## 4.高可用

![高可用](F:\typora文件\images\Snipaste_2023-10-15_14-42-21.png)

`需要两台Nginx服务器,keepalived软件与虚拟ip`

```shell
apt-get install keepalived
```







 
