#!/usr/bin/env sh

if [ $APP_START_SLEEP ]; then
  echo "启动程序等待$APP_START_SLEEP秒。。。"
  sleep $APP_START_SLEEP
fi

if [ -n "$JVM_DEBUG" ]; then
  echo "开启调试模式！"
  JVM_OPTS="$JVM_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
fi

if [ -n "$JAVA_JAR" ]; then
  echo "使用JAR包模式启动！"
  nohup java -server $JVM_OPTS $JAVA_OPTS -jar /app/app.jar > logs/app.log 2>&1 &
else
  echo "使用JAR包模式启动！"
  nohup java -server $JVM_OPTS $JAVA_OPTS org.springframework.boot.loader.JarLauncher > logs/app.log 2>&1 &
fi

# nohup java -server $JVM_OPTS $JAVA_OPTS org.springframework.boot.loader.JarLauncher > logs/app.log 2>&1 &
tail -f logs/app.log

