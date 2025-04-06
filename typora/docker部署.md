docker部署

防火墙

```shell
firewall-cmd --zone=public --add-port=8099/tcp --permanent
systemctl restart firewalld
firewall-cmd --zone=public --list-ports
```





## 1.单机部署

docker配置自己的阿里云镜像

```shell
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://l4zn8q9g.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

==docker清除各种缓存==

```shell
docker system prune -a
```



### 1.mysql

```shell
docker run -d -p 3307:3306 \
-e MYSQL_ROOT_PASSWORD=123456  --name mysql mysql:5.7
```

复制出mysql的配置信息

编码格式文件在/etc/mysql/mysql.conf.d/mysqld.cnf中

docker cp mysql:/etc/mysql/ /Users/ganqingyao/docker/volumes/mysql/conf

```shell
docker run -d \
-p 3306:3306 \
--privileged=true \
--restart=always  \
-v /Users/ganqingyao/docker/volumes/mysql/conf:/etc/mysql/conf.d \
-v /Users/ganqingyao/docker/volumes/mysql/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
--name mysql \
mysql:8.0.29
```

​	配置文件

```shell
[mysqld]
# 服务器唯一id，默认值1
server-id=1
# 设置日志格式，默认值ROW
binlog_format=STATEMENT
character-set-server=utf8
# 二进制日志名，默认binlog
# log-bin=binlog
# 设置需要复制的数据库，默认复制全部数据库
#binlog-do-db=mytestdb
# 设置不需要复制的数据库
#binlog-ignore-db=mysql
#binlog-ignore-db=infomation_schema
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```



```shell
docker run -d -p 3306:3306 --privileged=true \
--restart=always  \
-v /Users/ganqingyao/docker/volumes/mysql/log:/var/log/mysql \
-v /Users/ganqingyao/docker/volumes/mysql/data:/var/lib/mysql \
-v /Users/ganqingyao/docker/volumes/mysql/conf/mysql:/etc/mysql \
-v /Users/ganqingyao/docker/volumes/mysql/conf/my.cnf:/etc/my.cnf \
-e MYSQL_ROOT_PASSWORD=123456  --name mysql mysql
```

> **注意：需要修改mysql的编码格式**
> **`修改/app/mysql/conf/mysql/my.cnf`**，在其中添加以下配置，重启docker restart mysql就可以了
>
> ```shell
> [client]
> default-character-set=utf8
> [mysql]
> default-character-set=utf8
> [mysqld]
> character-set-server=utf8
> ```

重启mysql容器：docker restart mysql（容器名称）

登录容器：docker exec -it mysql /bin/bash

登录mysql：mysql -uroot -p

查看mysql编码格式：show variables like 'character%';

### 2.redis

```shell
docker run        \
-p 6379:6379        \
--name redis --privileged=true \
-v /Users/ganqingyao/docker/volumes/redis/redis.conf:/etc/redis/redis.conf \
-v /Users/ganqingyao/docker/volumes/redis/data:/data \
-d redis:6.0.8 redis-server /etc/redis/redis.conf
```

> **注意：需要保证redis配置文件与下载的镜像版本一致**
>
> **配置文件需要修改的位置**

*  3.1 开启redis验证    可选
      requirepass 123

*  3.2 允许redis外地连接  必须
       注释掉 # bind 127.0.0.1

*  3.3   daemonize no
       将daemonize yes注释起来或者 daemonize no设置，因为该配置和docker run中-d参数冲突，会导致容器一直启动失败

*  3.4 开启redis数据持久化  appendonly yes  可选
*  3.5 关闭保护模式 protected-mode no

redis版本下载地址：http://download.redis.io/releases/

### 3.minio

```shell
docker run -d \
   -p 9000:9000 \
   -p 9090:9090 \
   --name minio \
   -v /Users/ganqingyao/docker/volumes/minio/data:/data \
   -e "MINIO_ROOT_USER=minioadmin" \
   -e "MINIO_ROOT_PASSWORD=minioadmin" \
   minio/minio:RELEASE.2023-09-30T07-02-29Z server /data --console-address ":9090"
```

> **注意：minio密码设置不要过于简单不然容器启动不了**

java连接minio API：https://github.com/minio/minio-java

或者去官网查看SDK-API :https://minio.io/doc

### 4.rabbitmq

下载镜像，需要下载带有management的，带有web管理页面

```shell
docker pull rabbitmq:3-management
```

创建所需文件配置

```shell
mkdir -p /Users/ganqingyao/docker/volumes/rabbitmq/{data,conf,log}
# 创建完成之后要对所创建文件授权权限，都设置成777 否则在启动容器的时候容易失败
chmod -R 777 /mydata/rabbitmq

```

启动容器:创建临时容器

```shell
docker run \
 --restart=always  \
 -e RABBITMQ_DEFAULT_USER=admin \
 -e RABBITMQ_DEFAULT_PASS=123456 \
 --name rabbitmq \
 --hostname mq1 \
 -p 15672:15672 \
 -p 5672:5672 \
 -d \
 rabbitmq:3-management
```

数据持久化挂载数据卷(防止容器被误删)

```shell
docker run -d --name rabbitmq    \
--privileged=true --hostname=rabbitmqhost \
-e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=123456   \
-e RABBITMQ_DEFAULT_VHOST=my_vhost   \
-v /Users/ganqingyao/docker/volumes/rabbitmq/data:/var/lib/rabbitmq     \
-v /Users/ganqingyao/docker/volumes/rabbitmq/conf:/etc/rabbitmq   \
-v /Users/ganqingyao/docker/volumes/rabbitmq/log:/var/log/rabbitmq    \
-p 5672:5672 -p 15672:15672    \
rabbitmq:3-management

解释：
-e RABBITMQ_DEFAULT_VHOST=my_vhost \  :这是创建虚拟主机,可以指定，也可以不指定，默认是/
–restart=always ：表示随着Docker容器重启
-e ：指定环境变量 RABBITMQ_DEFAULT_VHOST：默认虚拟机名；RABBITMQ_DEFAULT_USER：默认的用户名；RABBITMQ_DEFAULT_PASS：默认用户名的密码，rabbitmq默认账号和密码是guest
--hostname ：主机名
-p ：端口映射
-v ：文件挂载
-d ：表示后台运行
–name rabbitmq ：表示启动后的容器实例名称为rabbitmq
```

> **注意：上方启动之后无法访问rabbitmq的web页面，需要进入rabbitmq**
>
> **1.docker exec -it rabbitmq /bin/bash**
>
> **2.进入目录之后执行： rabbitmq-plugins enable rabbitmq_management 命令**



### mongodb部署

创建文件夹

```shell
mkdir -p /Users/ganqingyao/docker/volumes/mongo/data
```



```shell
docker run -d --name mymongo -p 27017:27017  \
-v /Users/ganqingyao/docker/volumes/mongo/data:/data/db   \
mongo:7.0 --auth
```

> 进入容器
> docker exec -it mymongo mongosh admin

创建用户

```shell
db.createUser({ user: 'admin', pwd: '123456', roles: [ { role: "userAdminAnyDatabase", db: "admin" } ] });
db.updateUser({user: 'admin',roles:{role:'readWriteAnyDatabase',db:'admin'}});


```



### 5.nginx部署

创建挂载目录

```shell
# 创建挂载目录
mkdir -p /Users/ganqingyao/docker/volumes/nginx/conf
mkdir -p /Users/ganqingyao/docker/volumes/nginx/log
mkdir -p /Users/ganqingyao/docker/volumes/nginx/html
```



```shell
# 生成容器
docker run --name myNginx -p 9001:80 -d nginx
# 将容器nginx.conf文件复制到宿主机
docker cp myNginx:/etc/nginx/nginx.conf /Users/ganqingyao/docker/volumes/nginx/conf/nginx.conf
# 将容器conf.d文件夹下内容复制到宿主机
docker cp myNginx:/etc/nginx/conf.d /Users/ganqingyao/docker/volumes/nginx/conf/conf.d
# 将容器中的html文件夹复制到宿主机
docker cp myNginx:/usr/share/nginx/html /Users/ganqingyao/docker/volumes/nginx/
```

运行实例

```shell
docker run \
-p 80:80 \
--name mynginx \
-v /Users/ganqingyao/docker/volumes/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
-v /Users/ganqingyao/docker/volumes/nginx/conf/conf.d:/etc/nginx/conf.d \
-v /Users/ganqingyao/docker/volumes/nginx/log:/var/log/nginx \
-v /Users/ganqingyao/docker/volumes/nginx/html:/usr/share/nginx/html \
-d nginx:latest
```

### 6.gitlab部署

gitlab教程：https://docs.gitlab.cn/jh/install/docker.html

root密码所在位置：/etc/gitlab/initial_root_password下,这是新版本的密码所在地

创建挂载卷

```shell
mkdir -p /app/gitlab/{data,config,logs}
```

```shell
docker run -d  \
--privileged=true  \
--hostname 192.168.92.130  \
-p 443:443 -p 80:80 \
--name gitlab  \
--restart always   \
-v /app/gitlab/config:/etc/gitlab:Z  \
-v /app/gitlab/logs:/var/log/gitlab:Z  \
-v /app/gitlab/data:/var/opt/gitlab:Z   \
--shm-size 256m  \
gitlab/gitlab-ce:15.3.0-ce.0
```



```SHELL
docker pull gitlab/gitlab-ce:15.3.0-ce.0
```

旧版本的gitlab密码：

```shell
docker run -d  \
--privileged=true  \
--hostname 192.168.136.166  \
-p 443:443 -p 80:80 \
--name gitlab  \
--restart always   \
-v /app/gitlab/config:/etc/gitlab:Z  \
-v /app/gitlab/logs:/var/log/gitlab:Z  \
-v /app/gitlab/data:/var/opt/gitlab:Z   \
--shm-size 256m  \
--GITLAB_ROOT_PASSWORD="123nanfeng" \
gitlab/gitlab-ce:12.9.0-ce.0
```

**gitlab runner的docker安装**

```shell
docker pull gitlab/gitlab-runner:v15.3.3


docker run -itd --name gitlab-runner \
--privileged=true  \
--restart always \
  -v /app/gitlab-runner/config:/etc/gitlab-runner \
  -v /app/gitlab-runner/docker.sock:/var/run/docker.sock \
  gitlab/gitlab-runner:v15.3.3
```



```shell
docker pull gitlab/gitlab-runner::latest
mkdir -p /app/gitlab/{config,docker.sock}
docker pull gitlab/gitlab-runner:v15.3.3

docker run -d --name gitlab-runner \
--privileged=true  \
--restart always \
  -v /app/gitlab-runner/config:/etc/gitlab-runner \
  -v /app/gitlab-runner/docker.sock:/var/run/docker.sock \
  gitlab/gitlab-runner:v12.9.0 register \
  --non-interactive  \
  --executor "shell"  \
  --url "http://192.168.92.128/" \
  --registration-token "sQMzY6DiwEwdZEuorSfG" \
  --description "devops-runner" \
  --tag-list "build,deploy" \
  --run-untagged="true" \
  --locked="false" \
  --access-level="not_protected"
```

```shell
sudo docker exec gitlab-runner gitlab-runner register \
  --non-interactive  \
  --executor "shell"  \
  --url "http://192.168.92.130/" \
  --registration-token "A4rrceogNC5nz72gHwx6" \
  --description "docker-runner" \
  --tag-list "build,deploy" \
  --run-untagged="true" \
  --locked="false" \
  --access-level="not_protected"
```



```shell
sudo docker exec gitlab-runner gitlab-runner register -n \
       --url http://192.168.92.130/ \           #gitlab 的ip端口
       --registration-token sQMzY6DiwEwdZEuorSfG \  # gitlab 上的token
       --tag-list "global-runner,gitlab-test" \
       --executor docker \
       --docker-image docker \
       --docker-image alpine:latest \
       --docker-volumes /home/msq/gitlab-runner/.m2:/root/.m2 \
       --docker-volumes /home/msq/gitlab-runner/.npm:/root/.npm \
       --docker-volumes /var/run/docker.sock:/var/run/docker.sock \ #挂载出来的docker
       --description "global-runner,gitlab-test"
```

```yml
gitlab-runner register \
  --non-interactive \
  --executor "docker" \
  --docker-image alpine:latest \
  --url "http://192.168.92.130/" \
  --registration-token "A4rrceogNC5nz72gHwx6" \
  --description "docker-runner" \
  --tag-list "newdocker" \
  --run-untagged="true" \
  --locked="false" \
  --docker-privileged \ 
  --access-level="not_protected"
```



### 7.Jenkins部署

前提：不然启动不了`chmod 777 jenkins_home/`

官网提供的版本jenkins/jenkins:lts-jdk17有些插件安装不了

```shell
docker run -d \
--name jenkins  \
--privileged=true  \
-v /app/jenkins_home:/var/jenkins_home  \
-p 8080:8080 -p 50000:50000  \
--restart=always  \
jenkins/jenkins:lts-jdk17
```

#### 配置安装插件的镜像加速

```
vim /app/jenkins_home/hudson.model.UpdateCenter.xml
```

原始内容如下：

```xml
<?xml version='1.1' encoding='UTF-8'?>
<sites>
  <site>
    <id>default</id>
    <url>https://updates.jenkins.io/update-center.json</url>
  </site>
</sites>
```

url 修改为国内的清华大学官方镜像地址，最终内容如下：

```xml
<?xml version='1.1' encoding='UTF-8'?>
<sites>
  <site>
    <id>default</id>
    <url>https://mirrors.tuna.tsinghua.edu.cn/jenkins/updates/update-center.json</url>
  </site>
</sites>
```

需要安装新版本的Jenkins:2.426,才能解决插件无法安装问题

```shell
docker run -d --name jenkins  \
--privileged=true  \
--restart=always  \
-p 8080:8080 -p 50000:50000   \
-v /app/jenkins_home:/var/jenkins_home  \
--env JAVA_OPTS="-Duser.timezone=GMT+08"  \
jenkins/jenkins:2.426
```

```shell
docker cp /app/apache-maven-3.6.1/conf/settings.xml jenkins:/opt/maven3.6.3/conf
```

```shell
docker cp /app/apache-maven-3.6.1/repository jenkins:/opt/maven3.6.3
```

### 8.nacos

docker pull nacos/nacos-server:latest

```shell
docker run --name nacos -d -p 8848:8848 -e MODE=standalone  nacos/nacos-server:v2.2.3
```

```shell
docker cp nacos:/home/nacos/ /app/

```

```shell
#2.0以上的版本一下命令无法启动
docker run -d -p 8848:8848  \
--privileged=true  \
--restart=always  \
-v /app/nacos:/home/nacos  \
--env MODE=standalone  \
--name nacos \
nacos/nacos-server:v2.2.3
```

```shell
#2.0以上的版本启动方式，其中多余的端口映射时nacos要求的，加上即可，按照以下进行配置
docker run -d --name nacos \
--ip 192.168.136.166 \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--env MODE=standalone \
--env NACOS_AUTH_ENABLE=true \
-v /app/nacos/conf/:/home/nacos/conf \
-v /app/nacos/logs:/home/nacos/logs \
-v /app/nacos/data:/home/nacos/data \
nacos/nacos-server:v2.2.3
```

application.properties配置文件的内容

```properties
# spring
server.servlet.contextPath=/nacos
server.contextPath=/nacos
server.port=8848
server.tomcat.accesslog.max-days=30
server.tomcat.accesslog.pattern=%h %l %u %t "%r" %s %b %D %{User-Agent}i %{Request-Source}i
server.tomcat.accesslog.enabled=false
server.error.include-message=ALWAYS
# default current work dir
server.tomcat.basedir=file:.
#*************** Config Module Related Configurations ***************#
### Deprecated configuration property, it is recommended to use `spring.sql.init.platform` replaced.
#spring.datasource.platform=mysql
spring.sql.init.platform=mysql
nacos.cmdb.dumpTaskInterval=3600
nacos.cmdb.eventTaskInterval=10
nacos.cmdb.labelTaskInterval=300
nacos.cmdb.loadDataAtStart=false
db.num=1
db.url.0=jdbc:mysql://192.168.52.19:3306/nacos_config?characterEncoding=utf8&connectTimeout=10000&socketTimeout=3000&autoReconnect=true&useSSL=false
db.user.0=root
db.password.0=root
### The auth system to use, currently only 'nacos' and 'ldap' is supported:
nacos.core.auth.system.type=nacos
### worked when nacos.core.auth.system.type=nacos
### The token expiration in seconds:
nacos.core.auth.plugin.nacos.token.expire.seconds=18000
### The default token:
nacos.core.auth.plugin.nacos.token.secret.key=bmFjb3MxMjM0NTY3ODkxMjM0NTY3ODkxMjM0NTY3ODkxMjM0NTY3ODk=
### Turn on/off caching of auth information. By turning on this switch, the update of auth information would have a 15 seconds delay.
nacos.core.auth.caching.enabled=false
nacos.core.auth.enable.userAgentAuthWhite=false
nacos.core.auth.server.identity.key=nacoskey
nacos.core.auth.server.identity.value=nacosvalue
## spring security config
### turn off security
nacos.security.ignore.urls=${NACOS_SECURITY_IGNORE_URLS:/,/error,/**/*.css,/**/*.js,/**/*.html,/**/*.map,/**/*.svg,/**/*.png,/**/*.ico,/console-fe/public/**,/v1/auth/**,/v1/console/health/**,/actuator/**,/v1/console/server/**}
# metrics for elastic search
management.metrics.export.elastic.enabled=false
management.metrics.export.influx.enabled=false
nacos.naming.distro.taskDispatchThreadCount=10
nacos.naming.distro.taskDispatchPeriod=200
nacos.naming.distro.batchSyncKeyCount=1000
nacos.naming.distro.initDataRatio=0.9
nacos.naming.distro.syncRetryDelay=5000
nacos.naming.data.warmup=true
```



### 9.sentinel

```shell
docker pull bladex/sentinel-dashboard:1.8.0
```

```shell
docker run --name sentinel \
-d -p 8858:8858 -p 8719:8719 \
-e username=sentinel \
-e password=sentinel \
-e server=localhost:8858  \
bladex/sentinel-dashboard:1.8.0
```

```shell
docker update --restart=always sentinel
```



### 10.seata

docker pull seataio/seata-server:1.8.0

```shell
docker run --name seata -d \
--privileged=true  \
--restart=always  \
-e SEATA_PORT=8091 \
-p 8091:8091 \
seataio/seata-server:1.8.0
```



```shell
docker run --name seata -d \
--privileged=true  \
--restart=always  \
-e SEATA_PORT=8091 \
-p 8091:8091 \
-v /app/seata/config:/seata-server/resources \
seataio/seata-server:1.8.0
```

### kafka

```shell
docker run -d \
  --name kafka \
  -p 9092:9092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_CFG_PROCESS_ROLES=controller,broker \
  -e KAFKA_CFG_NODE_ID=1 \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@localhost:9093  \
  -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT \
  -e KAFKA_CFG_LOG_DIRS=/bitnami/kafka/logs \
  -e KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_KRAFT_CLUSTER_ID=cluster   \
  -v /Users/ganqingyao/docker/volumes/kafka/kafka-data:/bitnami/kafka \
  bitnami/kafka:3.9

```

###  frooodle/s-pdf

```shell
docker run -d \
  -p 8080:8080 \
  -v /Users/ganqingyao/docker/volumes/stirlingpdf/trainingData:/usr/share/tessdata \
  -v /Users/ganqingyao/docker/volumes/stirlingpdf/extraConfigs:/configs \
  -v /Users/ganqingyao/docker/volumes/stirlingpdf/logs:/logs \
  -e DOCKER_ENABLE_SECURITY=false \
  -e INSTALL_BOOK_AND_ADVANCED_HTML_OPS=false \
  -e LANGS=zh_CN \
  --name stirling-pdf \
  frooodle/s-pdf:latest
```

### linux 命令

```shell
docker run --name linux-command --rm -d -p 9665:3000 wcjiang/linux-command:latest
```



### 11.elasticsearch

docker pull elasticsearch:7.17.1

docker pull kibana:7.17.1

docker network create es-net

版本控制：

https://www.elastic.co/support/matrix#matrix_compatibility

```shell
docker run -d \
	--name es  \
	-e "ES_JAVA_OPTS=-Xms512m -Xmx512m"  \
	-e "discovery.type=single-node"  \
	-v /Users/ganqingyao/docker/volumes/elasticsearch/es-data:/usr/share/elasticsearch/data \
	-v /Users/ganqingyao/docker/volumes/elasticsearch/es-plugins:/usr/share/elasticsearch/plugins  \
	--privileged  \
	--network es-net  \
	-p 9200:9200  \
	-p 9300:9300  \
	elasticsearch:7.17.1
```

```shell
docker run -d \
--name kibana  \
-e ELASTICSEARCH_HOSTS=http://es:9200  \
--network=es-net  \
-p 5601:5601  \
kibana:7.17.1
```

### elasticsearch集群模式

创建数据卷

```shell
docker volume create es-data01
docker volume create es-data02
docker volume create es-data03
```

在 Docker 中，您可以通过以下步骤创建一个数据卷并指定该数据卷的目录：

------

### 1. 创建数据卷

使用 `docker volume create` 命令创建一个数据卷。

```bash
docker volume create my_volume
```

其中 `my_volume` 是您数据卷的名字。

------

### 2. 启动容器并挂载数据卷到指定目录

在运行容器时，使用 `-v` 或 `--mount` 参数将数据卷挂载到容器中的目录。

#### 使用 `-v` 参数：

```bash
docker run -d -v my_volume:/path/in/container --name my_container some_image
```

- `my_volume` 是数据卷的名字。
- `/path/in/container` 是容器内数据卷的挂载目录。
- `some_image` 是您运行的镜像名称。

#### 使用 `--mount` 参数（推荐）：

```bash
docker run -d --mount source=my_volume,target=/path/in/container --name my_container some_image
```

- `source=my_volume` 指定数据卷名称。
- `target=/path/in/container` 指定容器内挂载路径。

------

### 3. 检查数据卷

使用以下命令检查数据卷是否成功创建及其挂载信息：

```bash
docker volume inspect my_volume
```

您会看到类似如下的信息，说明数据卷被挂载到 Docker 主机上的某个目录：

```json
[
    {
        "Name": "my_volume",
        "Driver": "local",
        "Mountpoint": "/var/lib/docker/volumes/my_volume/_data",
        "CreatedAt": "2025-01-02T10:00:00Z",
        "Scope": "local"
    }
]
```

- `Mountpoint` 是数据卷在宿主机上的实际路径。

------

### 4. 在宿主机访问数据卷内容

数据卷的内容存储在宿主机的 `Mountpoint` 路径中，例如 `/var/lib/docker/volumes/my_volume/_data`。您可以直接在宿主机上查看或操作该目录下的文件。



使用docker compose

```shell
version: '3'
services:
  es01:
    image: elasticsearch:7.17.1
    container_name: es01
    environment:
      - node.name=es01
      - discovery.seed_host=es02,es03
      - cluster.initial_master_modes=es01,es03,es03
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - es-data01:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    networks:
      - es-net
  es02:
    image: elasticsearch:7.17.1
    container_name: es02
    environment:
      - node.name=es02
      - discovery.seed_host=es01,es03
      - cluster.initial_master_modes=es01,es03,es03
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - es-data02:/usr/share/elasticsearch/data
    ports:
      - 9201:9200
    networks:
      - es-net
  es03:
    image: elasticsearch:7.17.1
    container_name: es03
    environment:
      - node.name=es03
      - discovery.seed_host=es02,es01
      - cluster.initial_master_modes=es01,es03,es03
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - es-data01:/usr/share/elasticsearch/data
    ports:
      - 9202:9200
    networks:
      - es-net
volumes:
  es-data01:
    driver: local
  es-data02:
    driver: local
  es-data03:
    driver: local
networks:
  es-net:
    driver: bridge
```

使用cerebro工具连接

docker run -d -e CEREBRO_PORT=9500 --network=es-net -p 9500:9500 lmenezes/cerebro

### 11.nexus部署

```shell
docker pull sonatype/nexus3
```

```shell
mkdir /Users/ganqingyao/docker/volumes/nexus/nexus-data

```

```shell
docker run -d --name nexus   \ 
 -p 3081:8081     \   #nexus3网页端
 -p 3082:8082    \   #docker host
 -p 3083:8083     \  #docker proxy
 -p 3084:8084      \   #docker group
 -v /Users/ganqingyao/docker/volumes/nexus/nexus-data:/nexus-data   \
 sonatype/nexus3

```

```shell
docker run -d --name nexus -p 3081:8081 -p 3082:8082 -p 3083:8083 -p 3084:8084 -v /Users/ganqingyao/docker/volumes/nexus/nexus-data:/nexus-data sonatype/nexus3
```



### 12.maven构建模版并上传nexus

> 注意：上传j a r包与上传模版不同，j a r包上传直接打包上传即可，模版上传不同。

1>在maven项目的pom文件中添加插件

```xml
            <!-- 配置 maven-archetype-plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-archetype-plugin</artifactId>
                <version>3.2.0</version>
            </plugin>
```

2>打开项目终端输入以下命令

```shell
mvn archetype:create-from-project

##进入打包生成的archetype目录
cd target/generated-sources/archetype
mvn install
## 生成archetype- catalog.xml文件
mvn archetype:crawl
```

3>在target/generated-sources/archetype 目录下的po m文件中添加以下内容：

```xml
    <!--    使用nexus的maven库-->
    <distributionManagement>
        <repository>
            <!--            这个ID就是刚才setting.xml文件中的id -->
            <id>releases</id>
            <url>http://192.168.3.11:3081/repository/maven-releases/</url>
        </repository>
        <!--        仓库的路径-->
        <snapshotRepository>
            <!--            这个ID就是刚才setting.xml文件中的id -->
            <id>snapshots</id>
            <url>http://192.168.3.11:3081/repository/maven-snapshots/</url>
        </snapshotRepository>
    </distributionManagement>
```

4>并在该目录下执行命令：

```shell
mvn deloy #发布
```




## 2.dockerfile 生成docker镜像

### 自定义镜像mycentosjava8

### 示例1

* 下载jdk-8u171-linux-x64.tar.gz
* 准备编写Dockerfile   ,首字母D要大写

```shell
FROM centos    #继承哪个镜像
MAINTAINER nanfeng<nanfeng@126.com>
 
ENV MYPATH /usr/local   #定义变量
WORKDIR $MYPATH   #进入容器的落脚点
 
#安装vim编辑器
RUN yum -y install vim
#安装ifconfig命令查看网络IP
RUN yum -y install net-tools
#安装java8及lib库
RUN yum -y install glibc.i686
RUN mkdir /usr/local/java
#ADD 是相对路径jar,把jdk-8u171-linux-x64.tar.gz添加到容器中,安装包必须要和Dockerfile文件在同一位置
ADD jdk-8u171-linux-x64.tar.gz /usr/local/java/
#配置java环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_171
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH
 
EXPOSE 80   #暴露的端口
 
CMD echo $MYPATH  
CMD echo "success--------------ok"
CMD /bin/bash

```

* 构建镜像  docker build -t centosjava8:1.5 .       最后的点与前方有一个空格
* 运行容器 docker run -it centosjava8:1.5 /bin/bash

### 示例2

创建springboot项目并打成jar包，放到与Dockerfile文件同一个目录中

Dockerfile

```shell
# 基础镜像使用java,并不需要提前下载java:8的镜像
FROM java:8
# 作者
MAINTAINER nanfeng
# VOLUME 指定临时文件目录为/tmp，在主机/var/lib/docker目录下创建了一个临时文件并链接到容器的/tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为zzyy_docker.jar
ADD docker_boot-0.0.1-SNAPSHOT.jar nanfeng_docker.jar
# 运行jar包
RUN bash -c 'touch /nanfeng_docker.jar'
ENTRYPOINT ["java","-jar","/nanfeng_docker.jar"]
#暴露6001端口作为微服务
EXPOSE 6001
```

* docker build -t zzyy_docker:1.6 .
*  docker run -d -p 6001:6001 zzyy_docker:1.6



## 3.docker-compose编排容器

示例：mkdir /mydocker   文件夹，在该文件夹中创建docker-compose.yml文件，`需要在/mydocker 目录下运行以下命令：`

```shell
docker-compose up                           # 启动所有docker-compose服务
docker-compose up -d                        # 启动所有docker-compose服务并后台运行
```

yml示例：

```yaml
version: "3"  #docker-compose使用3版本的
 
services:  #固定写法
  microService:   #服务名称，同一网段的各服务之间使用该服务名进行通信，docker network中有提到
    image: zzyy_docker:1.6
    container_name: ms01
    ports:
      - "6001:8080"
    volumes:
      - /app/microService:/data
    networks: 
      - nanfeng_net 
    depends_on: #该服务依赖其他的服务启动之后，才会启动自身
      - redis
      - mysql
 
  redis:
    image: redis:6.0.8
    ports:
      - "6379:6379"
    volumes:
      - /app/redis/redis.conf:/etc/redis/redis.conf
      - /app/redis/data:/data
    networks: 
      - nanfeng_net
    command: redis-server /etc/redis/redis.conf
 
  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: '123456'
      MYSQL_ALLOW_EMPTY_PASSWORD: 'no'
      MYSQL_DATABASE: 'db2021'
      MYSQL_USER: 'zzyy'
      MYSQL_PASSWORD: 'zzyy123'
    ports:
       - "3306:3306"
    volumes:
       - /app/mysql/db:/var/lib/mysql
       - /app/mysql/conf/my.cnf:/etc/my.cnf
       - /app/mysql/init:/docker-entrypoint-initdb.d
    networks:
      - nanfeng_net
    command: --default-authentication-plugin=mysql_native_password #解决外部无法访问
 
networks: 
   nanfeng_net: 

```

以上服务中未添加container_name配置的会被自动起个容器名：`所在目录(/mydocker)+服务名(redis)+第几个(01)`





