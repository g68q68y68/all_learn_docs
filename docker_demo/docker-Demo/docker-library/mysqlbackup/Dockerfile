FROM mysql:5.7
MAINTAINER lifeilong@goldenit.com.cn

COPY config /app
WORKDIR app

ENV TZ="Asia/Shanghai"

RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime; \
    yum install -y crontabs; \
    yum clean all; \
    chmod +x docker-entrypoint.sh; \
    chmod +x mysql_backup_script.sh

ENTRYPOINT ["./docker-entrypoint.sh"]

VOLUME /app/mysqlbackup
