#!/usr/bin/env sh

#备份保存路径
backup_dir=/app/mysqlbackup
#日期
dd=$(date +%Y-%m-%d-%H-%M-%S)

#保存备份个数，默认备份31天数据
backup_number=31
# mysql信息
# mysql_host=
mysql_port=3306
mysql_username=root
# mysql_password=
#将要备份的数据库
#mysql_database=

#加载环境变量
source /app/profile

#如果文件夹不存在则创建
if [ ! -d $backup_dir ]; then
  mkdir -p $backup_dir
fi

#简单写法 mysqldump -u root -p123456 users > /root/mysqlbackup/users-$filename.sql
mysqldump -h$mysql_host -P$mysql_port -u$mysql_username -p$mysql_password $mysql_database >$backup_dir/mysqlbackup-$dd.sql

#写创建备份日志
echo "[$dd] create $backup_dir/mysqlbackup-$dd.dupm" >>/app/mysqlbackup.log

#找出需要删除的备份
delfile=$(ls -l -crt $backup_dir/*.sql | awk '{print $9 }' | head -1)

#判断现在的备份数量是否大于$backup_number
count=$(ls -l -crt $backup_dir/*.sql | awk '{print $9 }' | wc -l)

if [ $count -gt $backup_number ]; then
  #删除最早生成的备份，只保留number数量的备份
  rm $delfile
  #写删除文件日志
  echo "[$dd] delete $delfile" >>/app/mysqlbackup.log
fi
