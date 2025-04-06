# *git命令*

git config --system --list #系统配置

git config --global --list #全局个人配置

git config --global user.name "ganqingyao"

git config --global user.email 1421932626@qq.com

git init

git clone [url]

#查看所有文件状态

git status

#查看指定文件状态

git status [filename]

git add .

git commit -m "说明信息"

#生成公钥

ssh -keygen

ssh -keygen -t rsa

git push origin master #push到指定的分支

#列出所有指定分支

git branch

git pull :拉取代码

#列出所有远程分支

git branch -r

git branch [branch name] #新建本地一个分支，但依然停留在当然分支

 git branch -d dev #删除本地一个分支

 git branch -dr dev #删除远程一个分支

git merge [branch] #合并指定分支到当前分支



# linux命令

cd：进入一个文件目录 cd dev

cd .. :返回上一级目录

pwd :显示当前所在的目录路径

ls:列出所有当前目录中的所有文件

touch: 新建一个文件，touch index.js

rm: remove删除一个文件 rm index.js

mkdir:新建一个目录，mkdir dev

rm -r :删除一个目录。rm -r dev,删除Dev目录

mv [目标文件] [移动到的文件夹]注意是在同一个目录下

reset:重启终端

clear：清屏

history：查看历史命令

help:帮助

exit:退出

#：标识注释



## 解决yarn与vue不能在全局模式下运行的问题

管理员模式下powerShell运行一下命令：

Get-ExecutionPolicy -List

Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned -Force







