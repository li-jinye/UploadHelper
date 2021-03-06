# UploadHelper 包部署工具

一键将本地的项目包（war/jar）上传到服务器

只需要预先配置好服务器连接信息与包路径，一次配置，次次爽

还支持监听文件变动，自动上传部署项目包，懒人必备

**版本：**r1.0.0.1beta  **作者：**li-jinye  **语言：**Java

## 适用情况

- 简化包部署流程
- 有很多项目在开发，简化并统一管理包的部署
- 前后端联合开发/调试时，让改动立即生效
- 只想要简单上传包的功能，不想用或不会用SSH工具

## 流程优化

| 传统包部署流程                 | UploadHelper 包部署一次    | UploadHelper 包自动部署            |
| ------------------------------ | -------------------------- | ---------------------------------- |
| (项目打包，打开SSH工具)        | (项目打包，打开包部署工具) | (包部署工具启动自动部署，项目打包) |
| 1.连接服务器                   | 1.点击”部署一次“           | (自动部署)                         |
| 2.进入tomcat/webapps目录       |                            |                                    |
| 3.删除项目包（不删除无法上传） |                            |                                    |
| 4.上传项目包                   |                            |                                    |
| (等待项目启动)                 | (等待项目重启)             | (等待项目重启)                     |

将**四个**步骤简化成**一个**步骤，若开启自动部署功能，将做到全自动



## 疑难解答

**ssh工具可以连接成功，但是包部署工具连接失败**

有时连接失败，是因为服务器响应慢，提高连接超时时间(20秒以上)，可正常使用
若想彻底解决连接慢的问题，可通过修改配置文件的方式：修改 /etc/ssh/sshd_config文件，UseDns no

```
#UseDns yes
UseDns no
```

重启服务

```
service sshd restart
或者
/bin/systemctl restart sshd.service
```

