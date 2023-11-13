# spring-cloud
## 1.cloud-test: 
#### 一个简单的微服务项目，nacos注册中心（eurake已不用），orderserver基于openfeign远程调用userserver，使用gateway作为网关，sentinel服务监控限流降级
## 2.mq-demo
#### 包含rabbitmq以及springAMQP使用案例
## 3.seata-demo
#### 账户-订单-库存的分布式事务案例
## 4.hotel-admin&hotel-demo
#### 4.1 hotel-admin为酒店后台系统
##### 连接mysql，在后台系统的数据变更会以mq的形式同步给demo
#### 4.2 hotel-demo为酒店前台系统
##### 连接es，负责查询，并根据admin推送过来的消息，同步es数据库
## 5.hm-dianping
#### 一个使用redis的项目，包含
- springDataRedis（支持jedis，lettuce客户端）
- 用户token解决集群下的用户身份验证问题
- 解决缓存穿透
- 解决缓存击穿（逻辑过期方式和互斥锁）
- redis实现全局唯一id（mysql，雪花算法，uuid都可以实现）
- 多线程下的超卖问题，用悲观锁或者乐观锁（版本号法，cas法）解决
- redis基于setnx实现分布式锁（删除锁需要判断锁是不是自己的，再释放锁）
- 编写lua脚本确保判断，删除锁动作的一致性
- redisson可重入锁原理以及lua脚本分析
- redisson联锁解决主从一致性问题
- redis消息队列（基于list结构模拟，pubsub，stream）
- redis数据结构（string，list，hash，set，sortedset，geo，bitmap，hyperloglog）

## 6.redis-demo
#### redisTemplate连接哨兵，分片集群，设置读写分离

## 7.item-service
### 多级缓存项目
- 搭建见 相关资料/redis-nginx/案例导入说明.md
- item.sql为所需sql

### 7.1 本地进程缓存
- test/CaffeineTest：本地进程缓存
- ItemController：进程缓存的使用

### 7.2 nginx+OpenResty工具+lua语言编程，实现nginx查redis，tomcat，并存为nginx缓存
- redis-nginx/安装OpenResty.md
- redis-nginx/nginx/item-service-nginx-1.18.0中html为前端页面，此nginx不要放到中文目录下运行
- redis-nginx/nginx/openresty-nginx为所需的nginx配置
- redis-nginx/nginx/*.lua为对应的lua文件
- common.lua需放在/usr/local/openresty/lualib/下
- item.lua需放在/usr/local/openresty/lua/下

### 7.3 redis缓存预热
- RedisHandler

### 7.4 canal缓存同步
- canal.ItemHandler

## 8 mq-advanced-demo
### mq消息可靠问题，延迟消息，消息堆积，mq集群

- 生产者消息确认机制
- 消息持久化
- 消费者消息确认机制
- 消费者失败重试
- 延迟交换机插件delayExchange