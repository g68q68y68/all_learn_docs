#!/usr/bin/env sh

if [ ! -f /app/mysqlbackup.log ]; then
  echo " 初始化创建备份文件  " >>/app/mysqlbackup.log
fi

# 环境变量没有数据库ip报错
if [ ! $MYSQL_HOST ]; then
  echo "[error] 初始化缺少数据库ip变量 MYSQL_HOST=ip " >>/app/mysqlbackup.log
  exit 1
fi

# 环境变量没有数据库密码报错
if [ ! $MYSQL_PASSWORD ]; then
  echo "[error] 初始化缺少数据库密码变量 MYSQL_PASSWORD=password " >>/app/mysqlbackup.log
  exit 1
fi

# 环境变量没有数据库名称报错
if [ ! "$MYSQL_DATABASE" ]; then
  echo "[error] 初始化缺少数据库名称变量 MYSQL_DATABASE=database " >>/app/mysqlbackup.log
  exit 1
fi

# 创建备份环境变量
echo "# 数据库变量信息" >>/app/profile
echo "mysql_host=$MYSQL_HOST" >>/app/profile
echo "mysql_port=${MYSQL_PORT:-3306}" >>/app/profile
echo "mysql_username=${MYSQL_USERNAME:-root}" >>/app/profile
echo "mysql_password=$MYSQL_PASSWORD" >>/app/profile
echo "mysql_database=\"$MYSQL_DATABASE\"" >>/app/profile
echo "backup_number=${BACKUP_NUMBER:-31}" >>/app/profile

# 环境变量没有数据库名称报错，变量上需要带上""，防止变量值有空格报错
if [ ! "$CRONTAB_DEFINITION" ]; then
  CRONTAB_DEFINITION="0 2 * * *"
fi

# 创建定时任务
crond || true
echo "$CRONTAB_DEFINITION /app/mysql_backup_script.sh" >>/app/crontab
crontab -u root /app/crontab

tail -f /app/mysqlbackup.log
