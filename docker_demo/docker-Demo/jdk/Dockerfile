FROM openjdk:8-alpine
MAINTAINER lifeilong@goldenit.com.cn

WORKDIR app

COPY docker-entrypoint.sh ./

ENV TZ="Asia/Shanghai" \
    JVM_OPTS="-XX:MaxRAMPercentage=50.0" \
    SERVER_PORT="8080" \
    SPRING_PROFILES_ACTIVE="prod"
# 添加中国时区，以及字体（验证码BufferedImage.getGraphics报空）
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories; \
    apk add --no-cache tzdata ttf-dejavu fontconfig; \
    ln -sf /usr/share/zoneinfo/$TZ /etc/localtime; \
    echo $TZ > /etc/timezone; \
    chmod +x docker-entrypoint.sh; \
    mkdir logs; \
    echo $JVM_OPTS

VOLUME ["/app/logs"]

EXPOSE 8080 5005

ENTRYPOINT ["./docker-entrypoint.sh"]

