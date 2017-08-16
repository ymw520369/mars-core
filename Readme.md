###实现以下功能
1、节点状态管理，当前节点的内存，磁盘，CPU，网络等的使用情况
2、辅助创建游戏节点
3、辅助合并游戏节点
4、整理数据库备份，并上传至S3
5、整理游戏文本日志，压缩上传
6、辅助游戏节点的配置更新
7、辅助游戏节点的更新与重启

###使用 zookeeper 进行节点管理，zookeeper目录结构如下
|-node.root
 |-tsnode address1
   |-rpc address
     |-addrss data
   |-node info
     |-node info data
   |-node state
     |-node state data
 |-tsnode address2
   ...

###考虑使用如下格式配置文件
nodeInfo:{
	nodeName:"game node 1"
	dbUser:"root"
	dbPwd:"pwd"
	gameNodeNamingRules:"gameServer"
	nodeRootDir:"/home/tsixi"
}


###Rpc客户端设计思路，本程序使用Rpc调用使用长连接方式
1、从zookeeper中获取所有Rpc地址节点，并watcher监听，当连接改变时进行重连
2、Rpc作为

###集群设计要点
- 玩家连接上网关服务器以后，网关服务器创建GateSession用于处理玩家消息，在非断线重连的情况下，默认将消息转发到数据服务器
- 集群中服务器与服务器之间连接会话使用ClusterSession，消息通信格式使用ClusterMessage











