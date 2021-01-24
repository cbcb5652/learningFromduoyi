# Zookeeper

## 基本原理

> zookeeper是一个分布式服务框架，主要用来解决数据管理问题，如: 统一命名服务，状态同步服务，集群管理，分布式应用配置项的管理。
>
> 现成的使用实现共识，组管理，领导者选举和状态协议。

==zookeeper = 文件系统 + 监听通知机制==

### **设计目标**

zookeeper的数据保留在内存中，这意味着zookeeper可以实现高吞吐量和低延迟数。（zookeeper实施对高性能，高可用性，严格有序访问加以重视）

![image-20201224142730909](images/image-20201224142730909.png)

- **zookeeper is replicated：** 组成zookeeper的服务器彼此了解。维护内存中的状态图像，以及持久存储中的事务日志和快照。只要大多数服务器可用，zookeeper服务器将可用。客户端连接到单个zookeeper服务器。客户端维护着一个TCP连接，通过它发送请求，获取响应，获取监视事件并发送心跳。如果与服务器的TCP连接断开，则客户端将连接到其他服务器。
- **zookeeper is ordered：** zookeeper用一个反应所有zookeeper事务顺序的数字标记每个更新。后续操作可以使用该命令来实现更高级的抽象，例如同步原语。
- **zookeeper is fast:** 在读取为主的负载均衡中，特别快。可在数千台计算机上运行，并且在读取比写入更为常见的情况下，其性能最佳，比率约10:1

### 数据模型与分层空间

ZooKeeper提供的名称空间与标准文件系统的名称空间非常相似。名称是由斜杠（/）分隔的一系列路径元素。ZooKeeper命名空间中的每个节点都由路径标识。

zookeeper的层次命名空间

![image-20201224143915355](images/image-20201224143915355.png)

### 节点和短暂节点

zookeeper命名空间中的每个节点都可以具有与其关联的数据以及子节点。就像拥有一个文件系统一样，该文件系统也允许文件成为目录。（zookeeper目的：协调数据，状态信息，配置，位置信息等，因此每个节点上的数据通常很小，在1字节到千字节范围内）

znode的数据每次更改时，版本号都会增加。例如当客户端检索数据时，也会接受到数据的版本。

zookeeper有四种节点：

- 顺序临时（EPHEMERAL_SEQUENTIAL）
- 顺序永久（PERSISTENT_SEQUENTIAL）
- 非顺序临时（EPHEMERAL）
- 非顺序永久（PERSISTENT）

临时节点的意义：只要创建znode，当处于活动状态的时候，znode也存在，回话结束，将删除znode。可以用来维护，并通知该程序是否挂掉，选举新的master。（类似于redis的哨兵）

### 节点属性

> 每个znode都包含一系列属性，通过命令get，可以获得节点属性

![image-20210106100916314](images/image-20210106100916314.png)

**dataVersion:** 数据版本号，每次对节点进行set操作，dataVersion的值都会增加1（即使设置的是相同的数据），可有效避免了数据更新时出现的先后顺序问题。

**cVersion:** 子节点的版本号。当znode的子节点有变化时，cVersion的值就会增加1

**aclVersion:** ACL的版本号

**cZxid:** Znode创建的事务id

**mZxid:** Znode被修改的事务id，即每次对znode的修改都会更新mZxid

- 对于zk来说，每次的变化都会产生一个唯一的事务id，zxid(Zookeeper Transaction Id)。通过zxid，可以确定更新操作的先后顺序。例如，如果zxid1小于zxid2，说明zxid1操作先于zxid2发生，zxid对于整个zk都是唯一的。

**ctime:**  节点创建的时间戳

**ephemeralOwner:** 如果该节点为临时节点，ephemeralOwner值表示该节点绑定的session id. 如果不是,ephemeralOwner值为0







### 有条件的监听

> 类似于手表的概念:   一个监听器只能捕获一次事件

可以设置手表触发一次消失，或者永久存在，当触发手表监视后，客户端会收到一个数据包，说明znode已更改。如果客户端和其中一个zookeeper服务器之间的连接断开，则客户端将收到本地通知。 （当设置临时，触发一次就消失，设置永久时，触发不会被删除，并且会以递归的方式触发已注册的znode以及所有znode的更改）

- **一次性触发:** 数据修改之后，一个监视时间将发送给客户端、例如客户端执行getData("/znode1",true)，然后/znode1 的数据被更改或删除，则客户端将获得/znode1的监视事件。如果/znode1再次更改，则除非客户端进行了另一次读取以设置新的监视，否则不会发送任何监视事件。
- **发送给客户端**： 这意味着事件正在传递给客户端，但是可能未成功到达更改操作的返回码到达发起更改的客户端之前，该事件尚未到达。手表被异步发送给观察者。Zookeeper提供了订购保证：客户端将永远不会看到它为其设置了手表的更改，直到它第一次看到手表为止。网络延迟/其他因素可能导致不同的客户端在不同的时间看到监视并从更新中返回代码。关键是不同客户端看到的所有内容将具有一定的顺序。
- **设置手表的配置：**  指节点可以更改的不同方式。可以将zookeeper视为维护两个手表列表：数据手表和儿童手表。getData() 和 exist() 设置数据监视。getChildren()  设置儿童手表。或者，可以考虑根据数据返回的数据类型设置手表。getData() 和 exist() 返回有关节点数据信息。而getChildren() 返回子级列表。因此，setData() 将触发数据监视是否设置了znode、成功的create() 将触发正在创建的znode的数据监视，并触发父znode的子监视。成功的delete() 将同时触发要删除的znode数据监视和子监视（因为不能再有子监视） 以及父node的子监视。

```shell
get /hadoop watch     # 此条命令会监听这个node一次，当/hadoop发生变化的时候，会监听到
```

**监听器stat path[watch] **  （节点状态发生改变的时候，向客户端发出通知）

> 使用stat path[watch] 注册的监听器能够在节点状态发生改变的时候，向客户端发出通知

```shell
stat /hadoop watch
set /hadoop  112233
[zk: localhost:2181(CONNECTED) 63] 
WATCHER::
WatchedEvent state:SyncConnected type:NodeDataChanged path:/hadoop
```

**ls/ls2 捕获子节点的监听**  （监听该节点下所有子节点的增加和删除操作）

```shell
ls /hadoop watch
ls2 /hadoop watch
```

**watcher的特性**

| 特性           | 说明                                                         |
| -------------- | ------------------------------------------------------------ |
| 一次性         | watcher是一次性的，一旦触发就会移除，再次使用时需要重新注册  |
| 客户端顺序回调 | watcher回调的顺序串行化执行的，只有回调后客户端才能看到最新的数据状态，一个watcher回调逻辑不应该太多，以免影响别的watcher执行 |
| 轻量级         | watchevent是最小的通信单元，结构上只包含通知状态，事件类型和节点路径，并不会告诉数据节点变化前后的具体内容 |
| 时效性         | watcher只有在当前session彻底失效时才会失效，若在session有效期内快速重连成功，则watcher依然存在，仍可接收通知 |

**客户端与服务端的连接状态**

- keeperState： 通知状态
- SyncConnected： 客户端与服务器正常连接时
- Disconnected： 客户端与服务端断开连接时
- Expired： 会话session失效
- AuthFailed：身份认证失败时



### ACL权限控制  

> zookeeper的access control list 访问控制列表可以做到这一点。
>
> acl权限控制，使用scheme: Id: permission 来标识，主要包含3个方面:
>
> - 权限模式(scheme)： 授权的策略
> - 权限对象(id): 授权的对象
> - 权限(permission): 授予的权限
>
> -------
>
> 1. zookeeper的权限控制是基于每个znode节点的，需要对每个节点设置权限
> 2. 每个znode支持设置多种权限控制方案和多个权限
> 3. 子节点不会继承父节点的权限，客户端无权访问某节点，但可能可以访问它的子节点

```shell
setAcl /test2 ip:127.0.0.1:crwda          # 将节点权限设置为ip:127.0.0.1 可以对节点进行增删改查
```

**权限模式**

> 采用何种方式授权

| 方案   | 描述                                                  |
| ------ | ----------------------------------------------------- |
| world  | 只有一个用户：anyone，代表登录zookeeper所有人（默认） |
| ip     | 对客户端使用IP地址认证                                |
| auth   | 使用已添加认证的用户认证                              |
| digest | 使用用户名  密码 进行认证                             |

**授予权限**

![image-20201225155054095](images/image-20201225155054095.png)

**授权的相关命令**

| 命令    | 使用方式              | 描述         |
| ------- | --------------------- | ------------ |
| getAcl  | setAcl<path>          | 读取ACL权限  |
| setAcl  | setAcl<path><acl>     | 设置ACL权限  |
| addauth | addauth<scheme><auth> | 添加认证用户 |

```shell
#用户授权
addauth chenbin:123456
setAcl /node3 auth:chenbin:cdrwa
```

==可以采用多种模式进行授权==



### 简单的API

- create:   创建一个本地树的一个节点
- delete:   删除指定的一个节点
- exists:    查看本地树是否存在该节点
- get data： 查看节点对应的数据
- set data:    设置节点对应的数据
- get children:    检索节点的子节点列表
- sync:    等待数据传播





**文件系统**

zookeeper维护一个类似文件系统的数据结构

<img src="images/image-20201224094809988.png" alt="image-20201224094809988" style="zoom:50%;" />

每个子目录项如NameService都被称为znode(目录节点)，和文件系统一样，我们能够自由的增加，删除znode，在一个znode下增加，删除子znode，唯一的不同在于znode是可以存储数据的。

有以下四种类型的znode:

> - PERSISTENT - 持久化目录节点  		                                                       	(create /hello world)
>   - 客户端与zookeeper断开连接后，该节点依旧存在
> - PERSISTENT-SEQUENTIAL - 持久化顺序编号目录节点                             (create -s /hello world)
>   - 客户端与zookeeper断开连接后，该节点依旧存在，只是zookeeper给该节点名称进行顺序编号
> - EPHEMERAL - 临时目录节点                                                                        (create -p /hello world)   
>   - 客户端与zookeeper断开连接后，该节点被删除
> - EPHEMERAL_SEQUENTAL - 临时顺序编号目录节点                                  (create -e /hello world)  
>   - 客户端与zookeeper断开连接后，该节点被删除，只是zookeeper给该节点名称进行顺序编号
>
> ==临时节点不允许有子节点，并且使用get命令查看的时候ephemeralOwner = 0x37692f829560001 比较长，没有顺序，永久（ephemeralOwner = 0x0）==

**监听通知机制**

客户端注册监听它关心的目录节点，当目录节点发生变化（数据改变，删除，子目录节点增加/ 删除）时，zookeeper会通知客户端。

**zookeeper能做什么？**

- 应用配置管理
- 同一命名服务
- 状态同步服务
- 集群管理

==分布式配置管理==

> 假设我们的程序是分布式部署在多台机器上，如果我们改变程序的配置文件，需要逐台去修改，非常麻烦。
>
> 现在把这些配置全部放到zookeeper上去，保存在zookeeper的某个目录节点中，然后所有相关应用程序对这个目录节点进行监听，一旦配置信息发生变化，每个应用程序就会收到zookeeper的通知，然后从zookeeper获取新的配置信息应用到系统中。

![image-20201224101605667](images/image-20201224101605667.png)





----



## 单机安装

1. 配置JAVA环境， 检验环境:  java -version
2. 下载并解压zookeeper

```shell
cd /usr/local
wget http://archive.apache.org/dist/zookeeper/zookeeper-3.4.9/zookeeper-3.4.9.tar.gz
tar -zxvf zookeeper-3.4.9.tar.gz
cd zookeeper-3.4.9
```

3. 重命名配置文件zoo_sample.cfg

```shell
cp conf/zoo_sample.cfg conf/zoo.cfg
```

4.  启动zookeeper

```shell
bin/zkServer.sh start
```

5. 检测是否成功启动，用zookeeper客户端连接下服务端

```shell
bin/zkCli.sh
```



------

## 简单的使用

**zookeeper操作命令**

1.  使用ls来查看当前zookeeper中包含的内容

![image-20201224111305732](images/image-20201224111305732.png)

2. 新建一个znode节点，使用 create /zkPro myData

![image-20201224111415725](images/image-20201224111415725.png)

3. 使用get命令查看znode是否包含我们所创建的字符串

![image-20201224111559564](images/image-20201224111559564.png)

> **znode的Stat结构由以下字段组成**
>
> - cZxid: 导致创建此znode的更改zxid
> - mzxid: 上次修改此znode的zxid
> - pzxid：最后一次修改此znode子级的更改zxid
> - ctime： 创建znode的时间
> - mtime： 最后一次修改znode的时间
> - cversion： 此znode的版本号，使用set覆盖之后版本号会+1
> - aclversion： 此znode的ACL更改次数
> - ephemeralOwner： 如果znode是一个临时节点，则此znode的所有的会话ID。如果它不是临时节点，则它将为零
> - dataLength： 此znode的数据字段长度
> - numChildren： 此znode的子级数  



4.  通过set 命令来修改字符串

![image-20201224112048383](images/image-20201224112048383.png)

5.  删除

![image-20201224112132703](images/image-20201224112132703.png)

6.  新建一个节点  (/username chenbin)

![image-20201224112442454](images/image-20201224112442454.png)

6. springboot的使用 （配置pom文件）

```xml
  <!--zookeeper-->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.3.6</version>
        </dependency>
```

 添加如下代码

```java
package com.duoyi.demo;

import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperProSync implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception  {
        // zookeeper配置数据存放路径
        String path = "/username";
        // 连接zookeeper 并注册一个默认的监听器
        zk = new ZooKeeper("49.232.151.218:2181",5000,new ZookeeperProSync());
        // 等待zk连接成功的通知
        connectedSemaphore.await();
        // 获取path目录节点的配置数据，并注册默认的监听器
        System.out.println(new String(zk.getData(path,true,stat)));

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        // zk连接成功通知事件
        if (KeeperState.SyncConnected == event.getState()){
            if (EventType.None == event.getType() && null == event.getPath()){
                connectedSemaphore.countDown();
            }else if (event.getType() == EventType.NodeDataChanged){
                try{
                    System.out.println("配置已修改，新值为:"+new String(zk.getData(event.getPath(),true,stat)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
```

**启动之后，会读取到/username的值，并监听修改**

![image-20201224112737465](images/image-20201224112737465.png)



![image-20201224112743569](images/image-20201224112743569.png)

## 集群的搭建

1. **重命名配置文件 并复制多份**

```shell
# 进入到conf目录下, 新建三份配置文件
cp zoo_sample.cfg zoo-1.cfg  
cp zoo_sample.cfg zoo-2.cfg 
cp zoo_sample.cfg zoo-3.cfg 
```

2. **举例其中的一份进行修改**  zoo-1.cfg

```shell
# vim conf/zoo-1.cfg
#数据快照所在路径
dataDir=/dataDir=/usr/local/zookeeper/data/zookeeper1	
服务应用端口
clientPort=2181
# 集群配置
	# server.A = B:C:D
	# A: 是一个数字，表示这个服务器的编号
	# B：是这个服务器的ip地址
	# C：Zookeeper服务器之间的通信端口
	# D：Leader选举的端口

server.1=127.0.0.1:2888:3888		# 这里对应的 1 对应dataDir 路径下的myid 
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```

按照以上的步骤 配置三个文件

**配置说明:**

- tickTime: 这个时间是作为zookeeper服务器之间或客户端与服务端之间维持心跳时间间隔，也就是每个tickTime时间就会发送一个心跳
- initLimit: 这个配置是用来配置接受客户端(这里所说的客户端部署用户连接Zookeeper服务器的客户端，而是zookeeper服务器集群中连接到Leader的Follower服务器) 初始化连接时最长能忍受多少个心跳时间间隔数。当已经超过了10个心跳的时间（也就是tickTime）长度之后zookeeper服务器还没有收到客户端返回的信息，那么表明这个客户端连接失败。总的时间长度就是10*2000 = 20秒
- syncLimit： 这个配置项标识Leader 与Follower之间发送消息，请求和响应时间长度，最长不能超过多少个tickTime的时间长度，总的时间长度就是5*2000=10秒
- dataDir： 顾名思义就是zookeeper保存数据的目录，默认情况下，zookeeper将写数据的日志文件也保存在这个目录里
- clientPort： 这个端口就是客户端连接zookeeper服务器的端口，zookeeper会监听这个端口，接受客户端的访问请求。
- server.A = B:C:D:   其中A是一个数字，表示是第几号服务器；B是这个服务器的ip地址；C表示的是这个服务器与集群中的Leader服务器交换信息的端口；D表示的是万一集群中的Leader服务器挂了，需要一个端口来重新进行选举，选出一个新的Leader，而这个端口就是用来执行选举时服务器互相通信的端口。如果伪集群的配置方式，由于B都是一样，所以不同的zookeeper实例通信端口号不能一样，所以要给它们分配不同的端口号

3. **新建dataDir文件目录**

```shell
mkdir data/zookeeper1
vi myid 
1
#-----------------------
mkdir data/zookeeper2
vi myid 

#-----------------------
mkdir data/zookeeper3
vi myid 
3
```

**启动多台服务器**

![image-20201224115542057](images/image-20201224115542057.png)

**查看集群状态**

![image-20201224135014347](images/image-20201224135014347.png)

**一致性协议zab协议（zookeeper Atomic broadcast）原子广播协议。**

![image-20201226135606493](images/image-20201226135606493.png)

zab广播模式工作原理，通过类似两阶段提交协议的方式解决数据一致性问题

![image-20201226135657040](images/image-20201226135657040.png)

以上就算客户端连接的是Follower节点，发起写请求还是会将请求转发给Leader进行写入

1. leader从客户端接收到一个请求
2. leader生成一个新的事务并未这个事务生成一个唯一的ZXID
3. leader将这个事务提议（propose）发送给所有的follows 节点
4. follow节点将收到事务请求加入到历史队列（history queue）中，并发送出ack给leader
5. 当leader收到大多数follower（半数以上的节点）的ack消息，leader会发送commit请求
6. 当follower收到commit请求时，从历史队列中将事务请求commit

### leader选举

**服务器状态**

- looking：寻找状态，当服务器处于该状态时，它会认为集群中没有leader，因此需要进入leader选举状态
- leading： 领导者状态。表明当前服务器角色是leader
- following：跟随者状态。表明当前服务器角色是follower
- observing： 观察者状态，表明当前服务器角色是observer

**服务器启动时期的leader选举**

![image-20201226140628944](images/image-20201226140628944.png)

所以启动第一个，再启动第二个，第二个会成为leader。再启动第三个的时候已经存在leader，会随着变为follower

**服务器运行时期的选举**

![image-20201226141325790](images/image-20201226141325790.png)

**observer角色及其配置**

observer角色的特点：

1. 不参与集群的leader选举
2. 不参与集群中写数据时的ack反馈

为了使用observer，在任何想变成observer角色的配置文件中加入如下配置：

```java
peerType = observer
```

并在所有server的配置文件中，配置成observer模式的server的那行配置加上：observer，例如：

```xml
server.3 = 192.168.0.0:2289:3389:observer
```







------

## 需求分析

客户端有如下四个需求: 

> 1. 它接收如下参数：
>    - Zookeeper服务的地址
>    - 被监控的znode的名称
>    - 可执行命令参数
> 2. 它会取得znode上关联的数据，然后执行命令
> 3. 如果znode变化，客户端重新拉取数据，再次执行命令
> 4. 如果znode消失了，客户端杀掉进行的执行命令

接受用户输入的系统命令，然后监控zookeeper的znode，一旦znode存在，或者发生变化，程序会把znode最新的数据存入文件，然后一个线程执行用户的命令，同时还会起两个线程输出执行结果及日志。

**生活的例子**

警察抓坏人

- 警方安排如下
  - 组长A负责指挥
  - 警察B负责监控嫌疑人，并与组长A联络
  - 警察C，D，E，F埋伏在嫌疑人住所前后左右，准备实施抓捕
- 抓捕过程是这样的
  - 组长A下达命令安排后，B,C 各就各位（对象A 做初始化工作）
  - B开始监控嫌疑人，一旦嫌疑人进入警察布下的埋伏圈，则马上通知组长A（对象B为watcher，嫌疑人为被监听的znode。A注册B的listener，在B的监听回调，在B的监听回调中被触发）
  - 组长A得到通知后，马上命令C，D，E，F 执行抓捕。（C，D，E是被A调用干活的线程）



**Executor：** 程序的入口，负责初始化zookeeper，DataMonitor，把自己注册为DataMonitor的监听者，一旦DataMonitor监听到变化后，会通知它执行业务操作。（例子中的组长A，有几个内部类是前面说的警员C,D,E,F负责干活）=

> StreamWriter  继承Thread，以多线程的形式负责把执行的结果输出。 （相对于例子中的警察C,D,E,F）

- 实现watcher：
  - 监听zookeeper连接的变化，实现process()方法,把事件传递给DataMonitor处理。 
- 实现DataMonitor中定义的接口DataListener：
  - 实现exists()方法，处理znode变化的具体逻辑。
- 实现runnable类：
  - run()方法中阻塞主线程，让程序转为事件驱动。



**DataMonitor:**  负责监控znode，发现znode变化后，通知listener执行业务逻辑，同时再次监控znode。（例子中的警察B，负责监控犯人，并通知A）

> DataMonitorListener:   DataMonitor 一旦监控到znode的变化，立即调用自己持有的listener的exists（通知它的监听者）

- 实现watcher：
  - 监听znode变化。实现process()方法，通过zk.exist()方法再次监听，再次设置自己为zookeeper.exist()的回调（实现不断监听，事件驱动）。同时数据返回后，立即进入下面的回调函数处理
- 实现StatCallback：
  - 这是zookeeper.exist()操作回调对象。实现processResult()方法，调用DataMonitor持有的listener（也就是Excutor）的exists()方法执行逻辑。



**图解**

Executor和DataMonitor的关系如下：

![image-20201224200920125](images/image-20201224200920125.png)

两者通过Executor作为主入口，初始化DataMonitor和ZooKeeper对象后，阻塞主线程。转为事件驱动。即通过DataMonitor监控znode上的事件来驱动程序逻辑。

整个流程如下：

![image-20201224201017163](images/image-20201224201017163.png)

1. Excutor把自己注册为DataMonitor的监听
2. DataMonitor实现watcher接口，并监听znode
3. znode变化时，触发DataMonitor的监听回调
4. 回调中通过ZooKeeper.exist() 再次监听znode
5. 上一步exist的回调方法中，调用监听自己的Executor，执行业务逻辑6
6. Executor启新的线程执行命令
7. Executor启新的线程打印执行命令的输出

------



## javaAPI

**连接zookeeper**

```java
 ZooKeeper zooKeeper = new ZooKeeper("49.232.151.218:2181", 5000, new Watcher())
```

* arg1: 服务器的ip和端口
* arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
* arg3: 监视器对象

**创建节点**

```java
// 同步方式
create(String path,byte[] data,List<ACL> acl,CreateModel createMode);
// 异步方式
create(String path,byte[] data,List<ACL> acl,CreateModel createMode,
      AsyncCallback.StringCallback callBack,Object ctx);
```

- path: znode 路径
- data： 要存储在znode的数据
- acl：要创建节点的访问控制列表，
- createMode： 节点的类型，这是一个枚举
- callBack： 异步回调接口
- ctx：传递上下文参数

###  创建节点

```java
package com.duoyi.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.Temperature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKCreate {

    String IP = "49.232.151.218:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);

        /**
         * arg1: 服务器的ip和端口
         * arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
         * arg3: 监视器对象
         */
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功~");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }

    @Test
    public void create1() throws Exception{
        // arg1：节点的路径
        // arg2: 节点的数据
        // arg3: 权限列表   world:anyone:cdrwa
        // args4: 节点的类型  持久化节点
        zooKeeper.create("/create/node1","node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create2() throws Exception{
        zooKeeper.create("/create2/node2","node2".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE,CreateMode.PERSISTENT);
    }

    @Test
    public void create3() throws  Exception{
        // world授权模式
        //权限列表
        List<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("world","anyone");
        // 权限设置
        acls.add(new ACL(ZooDefs.Perms.READ,id));
        acls.add(new ACL(ZooDefs.Perms.WRITE,id));
        zooKeeper.create("/create/node3","node3".getBytes(),acls,CreateMode.PERSISTENT);
    }

    @Test
    public void create4() throws Exception{
        //ip 授权模式
        // 权限模式
        List<ACL> acls = new ArrayList<>();
        //  授权模式和授权对象
        Id id = new Id("ip","49.232.151.218");
        // 权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        zooKeeper.create("/create/node4","node4".getBytes(),acls,CreateMode.PERSISTENT);
    }

    @Test
    public void create5() throws Exception{
        zooKeeper.addAuthInfo("digest","chenbin:123456".getBytes());
        zooKeeper.create("/create/node5","node5".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.PERSISTENT);
    }

    @Test
    public void create6() throws Exception{
        //auth授权模式
        // 添加授权用户
        zooKeeper.addAuthInfo("digest","chenbin:123456".getBytes());
        // 权限列表
        List<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("auth","chenbin");
        // 权限设置
        acls.add(new ACL(ZooDefs.Perms.READ,id));
        zooKeeper.create("/create/node6","node6".getBytes(),acls,CreateMode.PERSISTENT);
    }

    @Test
    public void create7() throws Exception{
        //digest授权模式
        //权限列表
        List<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("digest","chenbin:12312ghfsdhjf");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        zooKeeper.create("/create/node7","node7".getBytes(),acls,CreateMode.PERSISTENT);
    }


    @Test
    public void create8() throws Exception{
        // 持久化顺序节点, 常见持久化 有序节点
        String result = zooKeeper.create("/create/node8","node8".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(result);
    }


    @Test
    public void create9() throws Exception{
        // 创建临时节点, 随着会话的消失而消失
        String result = zooKeeper.create("/create/node9","node9".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println(result);
    }

    @Test
    public void create10() throws Exception{
        // 临时顺序节点
        String result = zooKeeper.create("/create/node10","node10".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(result);
    }

    @Test
    public void create11() throws Exception{
        // 异步方式创建节点
        zooKeeper.create("/create/node11", "node11".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                //0 代表创建成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                // 节点的名字
                System.out.println(name);
                // 上下文参数
                System.out.println(ctx);

            }
        },"I am context");
        Thread.sleep(10000);
        System.out.println("结束");
    }
}
```

###  删除节点

```java
package com.duoyi.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKDelete {


    String IP = "49.232.151.218:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);

        /**
         * arg1: 服务器的ip和端口
         * arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
         * arg3: 监视器对象
         */
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功~");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }

    @Test
    public void delete1() throws Exception{
        // arg1 删除节点的节点路径
        // args2 数据版本信息，-1代表删除节点时不考虑版本信息
        zooKeeper.delete("/delete/node1",-1);
    }


    @Test
    public void delete2() throws Exception{
        // 异步删除
        zooKeeper.delete("/delete/node1", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                //0 代表删除成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                // 上下文参数对象
                System.out.println(ctx);
            }
        },"I am Context");
        Thread.sleep(10000);
        System.out.println("结束~");
    }

}
```

### 判断节点是否存在

```java
package com.duoyi.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKExists {

    String IP = "49.232.151.218:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);

        /**
         * arg1: 服务器的ip和端口
         * arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
         * arg3: 监视器对象
         */
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功~");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }


    @Test
    public void exist1() throws Exception{
        Stat stat = zooKeeper.exists("/exists1", false);
        System.out.println(stat);
    }


    @Test
    public void exist2() throws Exception{
        // 异步方式
        zooKeeper.exists("/exists1", false, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                //0 代表执行成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                // 上下文参数
                System.out.println( ctx);
                // 节点的版本信息
                System.out.println(stat.getVersion());

            }
        },"I am Context");
        Thread.sleep(10000);
        System.out.println("结束");
    }
}
```

### 获取节点

```java
package com.duoyi.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKGet {

    String IP = "49.232.151.218:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);

        /**
         * arg1: 服务器的ip和端口
         * arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
         * arg3: 监视器对象
         */
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功~");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }


    @Test
    public void get1() throws Exception{
        //arg1  节点的路径
        //arg3  读取节点属性的对象
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData("/get/node1", false, stat);
        // 打印数据
        System.out.println(new String(data));
        // 版本信息
        System.out.println(stat.getVersion());
    }


    @Test
    public void get2() throws Exception{
        //异步方式
        zooKeeper.getData("/get/node1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                //0 代表读取成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                //  数据
                System.out.println(new String(data));
                // 属性对象
                System.out.println(stat.getVersion());

            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("结束");
    }
}
```

### 获取子节点

```java
package com.duoyi.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKGetChildren {

    String IP = "49.232.151.218:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);

        /**
         * arg1: 服务器的ip和端口
         * arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
         * arg3: 监视器对象
         */
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功~");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }


    @Test
    public void get1() throws Exception{
        // arg1 节点路径
        List<String> children = zooKeeper.getChildren("/get", false);
        for (String str : children) {
            System.out.println(str);
        }
    }


    @Test
    public void get2() throws Exception{
        // 异步用法
        zooKeeper.getChildren("/get", false, new AsyncCallback.ChildrenCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, List<String> children) {
                //0 代表读取成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                //  上下文参数对象
                System.out.println( ctx);
                // 子节点信息
                for (String child : children) {
                    System.out.println(child);
                }
            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("结束");
    }
}
```

###  修改节点

```java
package com.duoyi.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKSet {

    String IP = "49.232.151.218:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);

        /**
         * arg1: 服务器的ip和端口
         * arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
         * arg3: 监视器对象
         */
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功~");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }

    @Test
    public void set1()throws Exception{
        // arg1： 节点的路径
        // arg2：修改的数据
        // arg3: 数据版本号，-1代表版本号不参与更新
        Stat stat = zooKeeper.setData("/set/node1", "node11".getBytes(), -1);
        System.out.println(stat.getAversion());
    }

    @Test
    public void set2()throws Exception{
        zooKeeper.setData("/set/node2", "node2".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                //0 代表创建成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                // 上下文参数对象
                System.out.println(ctx);
                // 属性描述对象
                System.out.println(stat.getAversion());
            }
        },"I am Context");
        Thread.sleep(10000);
        System.out.println("结束");
    }
}
```

### 配置中心

​	例子： 将数据库用户名密码放在配置文件中，配置文件信息进行缓存。

设计思路：

1. 连接zookeeper服务器
2. 读取zookeeper的配置信息，注册watcher监听器，存入本地变量
3. 当zookeeper中的配置信息发生变化时，通过watcher的回调方法捕获数据变化事件
4. 重新获取配置信息

> 实时去获取zookeeper里面的配置信息

```java
package com.duoyi.Config;

import com.duoyi.watcher.ZKConnectionWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class MyConfigCenter implements Watcher {

    String IP = "49.232.151.218:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    ZooKeeper zooKeeper;

    //用于本地化存储配置信息
    private String url;
    private String username;
    private String password;

    @Override
    public void process(WatchedEvent watchedEvent) {
        try{
            // 捕获事件状态
            if (watchedEvent.getType() == Event.EventType.None){
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }else if (watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("连接断开");
                }else if (watchedEvent.getState() == Event.KeeperState.Expired){
                    System.out.println("连接超时！");
                    zooKeeper = new ZooKeeper(IP,6000,new ZKConnectionWatcher());
                }else if (watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("验证失败");
                }
            }else if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
                // 当配置信息发生变化时 从新加载
                initValue();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MyConfigCenter(){
        initValue();
    }


    // 连接zookeeper服务器，读取配置信息
    public void initValue(){
        try{
            // 创建连接对象
            zooKeeper = new ZooKeeper(IP, 5000, this);
            //阻塞线程 等待连接创建
            countDownLatch.await();
            // 读取配置信息
            this.url = new String(zooKeeper.getData("/config/url",true,null));
            this.username = new String(zooKeeper.getData("/config/username",true,null));
            this.password = new String(zooKeeper.getData("/config/password",true,null));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try{
            MyConfigCenter myConfig = new MyConfigCenter();
            for (int i = 1; i <= 10; i++) {
                Thread.sleep(5000);
                System.out.println("url:"+myConfig.getUrl());
                System.out.println("username:"+myConfig.getUsername());
                System.out.println("password:"+myConfig.getPassword());
                System.out.println("====================================");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

### 唯一ID

> 在过去的单库数据库中，通常指定自动递增的字段来生成唯一id，但是如果分库分表之后就没办法了。此时就可以用zookeeper在分布式环境下生成全局唯一id。

设计思路：

1. 连接zookeeper服务器
2. 指定路径生成临时有序节点
3. 取序列号及为分布式环境下的唯一ID

## 分布式锁

> 基于数据库实现的分布式锁，基于缓存实现分布式锁，基于zookeeper实现分布式锁。

### 为什么要分布式锁

   系统A是一个电商系统，只有一台服务器，系统用户下单的时候需要去订单接口看一下该商品是否有库存，如果有库存才能正常下单。

由于系统有一定的并发，需要将数据暂时缓冲在redis中，用户下单的时候更新数据库

架构如下

1. 第一步

<img src="images/image-20201225204453751.png" alt="image-20201225204453751" style="zoom: 80%;" />

> 这样一来产生一个问题：假如某个时刻，redis里面的商品库存为1。
>
> 此时两个请求进来，其中一个请求执行到上面的步骤3，更新数据库的库存为0，但是第4步还没有执行。
>
> 而另一个请求执行到了第2步，发现库存还是1，就继续执行第3步。==这样就出错了==

2. 第二步

> 解决方案： 用锁把2,3,4步锁住，让他们执行完之后，另一个线程才能进来执行第2步。

![image-20201225205316473](images/image-20201225205316473.png)

3. 一台机器扛不住了要另一台机器

![image-20201225205354733](images/image-20201225205354733.png)

增加机器后 该问题 还是没有解决了。依然会出现超卖的问题。

> 因为是运行在两个不同的JVM里面，他们加的锁只对属于自己的线程有效，对于其他线程是无效的。

**综上**

分布式锁的思路就是：在整个系统提供一个全局，唯一获取锁的“东西”，然后每个系统在需要加锁时，都去问这个“东西”拿到一把锁，这样不同的系统拿到的就可以认为是同一把锁。

这个东西可以是： **Redis**，**Mysql**，**zookeeper**

![image-20201225210302449](images/image-20201225210302449.png)

参考连接：https://zhuanlan.zhihu.com/p/73807097

-----

### Mysql分布式锁

> 直接创建一张锁表，然后通过操作该表中的数据来实现。（当我们要锁住某个方法或资源时），我们就在该表中增加一条记录，想要释放锁的时候就删除这条记录。

创建如下这张表：

![image-20201228165657518](images/image-20201228165657518.png)

当我们需要锁住某个方法的时候，可以执行以下SQL插入数据

```sql
insert into methodLock(method_name,desc) values('method_name','desc')
```

我们做了method_name的唯一约束，当多个请求提交到数据库的时候，只会保证有一个操作成功。操作成功就获得了该方法的锁，可执行方法体内容

当需要释放的时候执行以下SQL

```sql
delete from methodLock where method_name = 'method_name'
```

这种方法存在几个问题

> 1. 这把锁强依赖于数据库，数据库是一个单点，一旦数据库挂掉，就会导致业务系统不可用								==数据库集群==
>
> 2. 这把锁没有失效时间，一旦解锁操作失败，就会导致锁记录一直在数据库中，其他线程无法获取锁                    ==定时任务删除锁==
>
> 3. 这把锁只能是非阻塞的，一旦insert插入失败会直接报错。没有获取锁的线程并不会进入队列，想要再次获取锁就要再次触发获得锁操作    
>
>     ==while循环等待插入成功==
>
> 4. 这把锁是非重入的，同一个线程在没有释放锁之前无法再次获得该锁。因为数据库中数据已经存在了               ==数据库加个字段来判断==

-----

**基于数据库排他锁**

> 除了可以通过增删操作数据表中的记录以外，其实还可以借助数据中自带的锁来实现分布式锁。
>
> 我们还是基于刚刚创建的那张数据库表。可以通过数据库的排他锁来实现分布式锁。基于Mysql的InnoDB引擎，可以使用以下方法来实现枷锁操作。

![image-20201228172652090](images/image-20201228172652090.png)

在查询语句后面增加for update，数据会在查询过程中给数据表增加排他锁（Innodb引擎在加锁的时候，只有通过索引进行检索的时候才会使用行级锁，否则使用表级锁）记录的时候需要把方法的参数及参数类型也带上，避免出现重载方法之间无法同时被调用的问题。

我们可以认为获得排他锁的线程即可获得分布式锁，当获取到锁的时候可以执行方法的业务逻辑，执行完方法之后，通过以下方法解锁

```java
public void unlock(){
    connection.commit();
}
```

这种方法可以有效的解决上面的问题：

- 阻塞锁？ for update语句会在执行成功后立即返回，在执行失败时一直处于阻塞状态，直到成功
- 锁定之后服务宕机，无法释放？ 使用这种方式，服务器宕机之后数据库会自己把锁释放。

**总结**

> 两种方式都是依赖一张数据表，一张是通过表中记录的存在情况确定当前是否有所的存在，另外一种是通过数据库的排他锁来实现分布式锁。

优点：

- 直接借助数据库，容易理解

缺点：

- 会有各种各样的问题，在解决问题的过程中会使整个方案变得越来越复杂
- 操作数据库需要一定的开销，性能问题需要考虑
- 使用数据库的行级锁不一定靠谱，尤其是当我们的锁表并不大的时候



### Redis分布式锁

> 使用redis实现比数据库实现要好，性能要高于数据库。而且很多缓存是可以集群部署的，可以解决单点问题。

但是该实现方式存在几个问题：

> 1. 原子性操作的话 这把锁没有失效时间，一旦解锁操作失败，就会导致锁记录一致在，其他线程无法枷锁。
> 2. 这把锁只能是非阻塞的，无论是成功还是失败都是直接返回。
> 3. 这把锁是非重入的，一个线程获得锁之后，在释放锁之前，无法再次获得该锁，因为使用到key在redis中已经存在，无法再执行put操作

可以使用以下方案来解决

> - 可以给键设置过期时间，但是这样的话却无法保证原子性
> - 非阻塞，while重复执行。
> - 非可重入？在一个线程获取到锁之后，把当前主机信息和线程信息保存起来，下次在获取之前先检查是不是当前锁的拥有者。

==但是，失效时间设置为多长好呢？失效时间太短，方法还没执行完，锁就自动释放了。如果设置时间太长，其他获取锁的线程可能要平白多等一段时间。==

**总结**

可以使用缓存来代替数据库实现分布式锁，可以提高性能，可以避免单点问题。redis提供了可以用实现分布式锁的方法，使用setnx方法。并且这些缓存服务也都提供了对数据的过期自动删除的支持，可以直接设置超时时间来控制锁的释放。

优点： 性能好，实现起来比较方便

缺点： 通过时间来控制锁，十分的不方便。



### zookeeper分布式锁

设计思路：

1. 每个客户端往`/Locks` 下创建临时有序节点 ` /Locks/Lock_`, 创建成功后`/Locks` 下面会有每个客户端对应的节点  `/Lock/lock_0000001`
2. 客户端取得`/Locks` 下面的所有节点，并将节点排序，如果自己排在第一位表示枷锁成功
3. 如果自己不在第一位，则监听自己前一位的锁节点。例如，自己的锁节点`Locks/Lock_000000002` 则监听`Locks/Lock_000000001`
4. 当前一位锁节点 ``Locks/Lock_000000001` 对应的客户端释放了锁，将会触发监听客户端 `Locks/Lock_000000002`   的逻辑
5. 监听客户端重新执行第2步逻辑，判断自己是否获得了锁。 

**分布式锁**

```java
package com.duoyi.Config;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 分布式锁
 */
public class MyLock {

    String IP = "49.232.151.218:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    ZooKeeper zooKeeper;
    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "Lock_";
    private String lockPath;

    // 打开zookeeper连接
    public MyLock() {
        try {
            zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.None) {
                        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                            System.out.println("连接成功~");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取锁
    public void acquireLock() throws Exception {
        // 创建锁节点
        createLock();
        // 尝试获取锁
        attemptLock();
    }

    // 创建锁节点
    private void createLock() throws Exception {
        //判断Locks是否存在，不存在则创建
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH, false);
        if (stat == null) {
            zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 创建临时有序节点
        lockPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("节点创建成功：" + lockPath);
    }

    // 监视器对象，监视上一个节点
    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    };

    // 尝试获取锁
    private void attemptLock() throws Exception {

        // 获取Locks节点下的所有节点
        List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH, false);
        // 对子节点进行排序
        Collections.sort(list);
        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length() + 1));
        if (index == 0) {
            System.out.println("获取锁成功~");
            return;
        } else {
            // 上一个节点的路径
            String path = list.get(index - 1);
            Stat stat = zooKeeper.exists(LOCK_ROOT_PATH + "/" + path, watcher);
            if (stat == null) {
                attemptLock();
            } else {
                synchronized (watcher) {
                    watcher.wait();
                }
            }
            attemptLock();
        }

    }

    //释放锁
    public void releaseLock() throws Exception {
        zooKeeper.delete(this.lockPath, -1);
        zooKeeper.close();
        System.out.println("锁已经释放了~");
    }


    public static void main(String[] args) {
        try {
            MyLock myLock = new MyLock();
            myLock.createLock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**客户端**

```java
package com.duoyi.Config;

public class TicketSeller {

    private void sell(){
        System.out.println("售票开始");
        int sleepMillis = 5000;
        try{
            Thread.sleep(sleepMillis);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("售票结束");
    }

    private void sellTicketWithLock() throws Exception{
        // 使sell方法 锁住
        MyLock myLock = new MyLock();
        // 获取锁
        myLock.acquireLock();
        sell();
        // 释放锁
        myLock.releaseLock();
    }

    public static void main(String[] args) throws Exception {
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0; i < 10; i++) {
            ticketSeller.sellTicketWithLock();
        }
    }
}
```

**缺点：**

- 性能上没有缓存服务器那么高，（因为每次创建锁和释放锁的过程，都要动态创建，销毁瞬时节点来实现锁功能。）都需要由Leader服务器来控制
- 也有可能带来并发问题。（有可能因为网络抖动，zk集群以为session连接断了，zk以为客户端断了，这时候断开连接，其他客户端就会获取到锁，就产生了并发问题。）。这个问题不常见是因为zk有重试机制。

**总结**

- 优点：有效的解决单点问题，不重入问题，非阻塞问题以及锁无法释放的问题。实现起来比较简单 
- 缺点：性能上步入缓存式分布式锁，需要对zk有所理解。

-------

以上三种方案比较

 **从理解的难易程度角度（从低到高）**

数据库 > 缓存 > Zookeeper

**从实现的复杂性角度（从低到高）**

Zookeeper >= 缓存 > 数据库

**从性能角度（从高到低）**

缓存 > Zookeeper >= 数据库

**从可靠性角度（从高到低）**

Zookeeper > 缓存 > 数据库



## curator

> curator框架在zookeeper原生API接口上进行包装，解决了很多zookeeper客户端非常底层的细节开发。提供zookeeper各种应用场景（比如：分布式锁服务，集群领导选举，共享计数器，缓存机制，分布式队列等）的抽象封装，实现了Fluent风格的API接口，是最好用，最流行的zookeeper的客户端。

原生zookeeperAPI的不足：

- 连接对象异步创建，需要开发人员自行编码等待
- 连接没有自动重连超时机制
- watcher一次注册生效一次
- 不支持递归创建树型节点

curator特点：

- 解决session会话超时重连问题
- watcher反复注册
- 简化开发API
- 遵循Fluent风格的API
- 提供了分布式锁服务，共享计数器，缓存机制等机制

### **创建连接及重连策略**

```java
package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;

public class CuratorConnection {

    public static void main(String[] args) {

        //3秒后重连
        RetryPolicy retryPolicy = new RetryOneTime(3000);

        // 每3秒重连一次，重连3此
        RetryPolicy retryPolicy1 = new RetryNTimes(3,3000);

        //每3秒重连一次，总等待时间超过10秒后停止重连
        RetryPolicy retryPolicy2 = new RetryUntilElapsed(1000,3000);

        //baseSleepTimeMs * Math.max(1,random.nextInt(1<<(retryCount + 1))
        RetryPolicy retryPolicy3 = new ExponentialBackoffRetry(1000,3);

        // 创建连接对象
        CuratorFramework client = CuratorFrameworkFactory.builder()
                // 地址端口号
                .connectString("49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183")
                // 会话超时时间
                .sessionTimeoutMs(5000)
                // 重连机制
                .retryPolicy(retryPolicy3)
                // 命名空间
                .namespace("create")
                // 构建连接对象
                .build();

        // 打开连接
        client.start();
        System.out.println(client.isStarted());
        // 关闭连接
        client.close();
    }
}
```

### **创建create**

```java
package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.checkerframework.checker.units.qual.A;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CuratorCreate {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        client.start();
    }

    @After
    public void after(){
        client.close();
    }


    @Test
    public void create1() throws Exception{
        // 新增节点
        client.create()
                // 节点的类型
                .withMode(CreateMode.PERSISTENT)
                // 节点的权限列表  world:anyone:cdrwa
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                //arg1: 节点的路径
                //args2: 节点的数据
        .forPath("/node1","node1".getBytes());
        System.out.println("结束");
    }


    @Test
    public void create2() throws Exception{
        // 自定义权限类
        // 创建权限列表
        List<ACL> list = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("ip","49.232.151.218");
        list.add(new ACL(ZooDefs.Perms.ALL,id));
        client.create().withMode(CreateMode.PERSISTENT).withACL(list).forPath("/node2","node2".getBytes());
        System.out.println("结束");
    }


    @Test
    public void create3()throws Exception{
        // 递归创建节点树
        client.create()
                .creatingParentsIfNeeded()   // 递归创建父节点（父节点不存在的话）
                .withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath("node3/node31","node31".getBytes());
        System.out.println("结束");
    }

    @Test
    public void create4() throws Exception{
        // 异步创建
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                // 异步创建  回调接口
        .inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println(curatorEvent.getPath());
                System.out.println(curatorEvent.getType());
            }
        }).forPath("/node4","node4".getBytes());
        Thread.sleep(5000);
        System.out.println("结束");
    }
}
```

### 删除delete

```java
package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorDelete {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        client.start();
    }

    @After
    public void after(){
        client.close();
    }


    @Test
    public void delete1() throws Exception{
        // 删除节点
        client.delete().forPath("/node1");
        System.out.println("结束");
    }


    @Test
    public void delete2() throws Exception{
        client.delete()
                // 版本号
                .withVersion(4).forPath("/node1");
        System.out.println("结束");
    }


    @Test
    public void delete3()throws Exception{
        // 删除包含子节点的节点
        client.delete()
                .deletingChildrenIfNeeded()
                .withVersion(-1).forPath("/node1");
        System.out.println("结束");
    }

    @Test
    public void delete4() throws Exception{
        // 异步创建
       client.delete().deletingChildrenIfNeeded()
               .withVersion(-1)
               .inBackground(new BackgroundCallback() {
                   @Override
                   public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        //节点路径
                       System.out.println(curatorEvent.getPath());
                       //时间类型
                       System.out.println(curatorEvent.getType());
                   }
               }).forPath("/node1");
        Thread.sleep(5000);
        System.out.println("结束");
    }
}
```

### 判断存在Exists

```java
package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorExists {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("get")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }


    @Test
    public void exist1() throws Exception {
       // 判断节点是否存在
        Stat stat = client.checkExists()
                // 节点路径
                .forPath("/node3");
        System.out.println(stat);
    }


    @Test
    public void exist2() throws Exception {
        // 异步方式判断节点是否存在
        client.checkExists().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                // 节点路径
                System.out.println(curatorEvent.getPath());
                // 事件类型
                System.out.println(curatorEvent.getType());
                System.out.println(curatorEvent.getStat().getVersion());
            }
        }).forPath("/node2");
        Thread.sleep(5000);
        System.out.println("结束");
    }
}
```

### 获取Get

```java
package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorGet {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("get")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }


    @Test
    public void get1() throws Exception {
        // 读取数据
        byte[] bytes = client.getData().forPath("/node1");
        System.out.println(new String(bytes));
    }


    @Test
    public void get2() throws Exception {
        // 读取节点属性
        Stat stat = new Stat();
        byte[] bytes = client.getData()
                // 读取属性
                .storingStatIn(stat)
                .forPath("/node1");
        System.out.println(new String(bytes));
        System.out.println(stat.getVersion());
    }


    @Test
    public void get3() throws Exception {
       // 异步方式读取
        client.getData().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                //节点路径
                System.out.println(curatorEvent.getPath());
                //事件类型
                System.out.println(curatorEvent.getType());
                // 数据
                System.out.println(new String(curatorEvent.getData()));
            }
        }).forPath("/node1");
        Thread.sleep(5000);
        System.out.println("结束");
    }

    @Test
    public void delete4() throws Exception {
        // 异步创建
        client.delete().deletingChildrenIfNeeded()
                .withVersion(-1)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        //节点路径
                        System.out.println(curatorEvent.getPath());
                        //时间类型
                        System.out.println(curatorEvent.getType());
                    }
                }).forPath("/node1");
        Thread.sleep(5000);
        System.out.println("结束");
    }
}
```

### 获取子节点getChild

```java
package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorGetChild {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("get")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }


    @Test
    public void getChild1() throws Exception {
        // 读取子节点数据
        List<String> list = client.getChildren()
                // 节点路径
                .forPath("/get");
        list.forEach(l ->{
            System.out.println(l);
        });
    }


    @Test
    public void getChild2() throws Exception {
        // 异步方式读取子节点数据
        client.getChildren()
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        // 节点路径
                        System.out.println(curatorEvent.getPath());
                        // 数据类型
                        System.out.println(curatorEvent.getType());
                        // 读取子节点数据
                        List<String> list = curatorEvent.getChildren();
                        for (String s : list) {
                            System.out.println(s);
                        }
                    }
                }).forPath("/get");
        Thread.sleep(5000);
        System.out.println("结束");
    }
}
```

### 修改set

```java
package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CuratorSet {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        client.start();
    }

    @After
    public void after(){
        client.close();
    }


    @Test
    public void set1() throws Exception{
        // 更新节点
        client.setData()
                // arg1 节点的路径
                // arg2 节点的数据
                .forPath("/node1","node11".getBytes());
    }


    @Test
    public void set2() throws Exception{
        client.setData()
                // 指定版本号
                .withVersion(-1).forPath("/node1","node111".getBytes());
    }


    @Test
    public void set3()throws Exception{
       // 异步方式修改节点数据
        client.setData().withVersion(-1).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                // 节点的路径
                System.out.println(curatorEvent.getPath());
                // 事件类型
                System.out.println(curatorEvent.getType());
            }
        }).forPath("/node1","node1".getBytes());
        Thread.sleep(5000);
        System.out.println("结束");
    }

    @Test
    public void create4() throws Exception{
        // 异步创建
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                // 异步创建  回调接口
        .inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println(curatorEvent.getPath());
                System.out.println(curatorEvent.getType());
            }
        }).forPath("/node4","node4".getBytes());
        Thread.sleep(5000);
        System.out.println("结束");
    }
}
```

### 监控命令

![image-20201226162156295](images/image-20201226162156295.png)

**==conf命令==**

**查看zookeeper的配置信息**

```xml
echo  conf | nc localhost 2181
```



![image-20201226162958844](images/image-20201226162958844.png)

![image-20201226163032103](images/image-20201226163032103.png)

**==cons命令==**

```shell
echo cons | nc localhost 2181
```

![image-20201226163410008](images/image-20201226163410008.png)

**==crst命令==**

crst：重置当前这台服务器所有连接/ 会话的统计信息

shell终端输入：echo crst | nc localhost 2181

**==dump命令==**

dump：列出未经处理的会话和临时节点

shell终端输入：echo dump | nc localhost 2181



| 属性        | 含义                                                 |
| ----------- | ---------------------------------------------------- |
| session  id | znode path(1对多，处于队列中排队的session和临时节点) |

**==envi命令==**

envi：输出关于服务器的环境配置信息

shell终端输入：echo envi | nc localhost 2181

![image-20201226171720797](images/image-20201226171720797.png)

**==ruok命令==**

ruok：测试服务器是否处于正在运行状态

shell终端输入：

```shell
echo ruok | nc localhost 2181
```

**==stat命令==**

stat：输出服务器的详细信息与srvr相似，但是多了每个链接的会话信息

shell终端输入：

```shell
echo stat | nc locahost 2181
```

![image-20201226172201502](images/image-20201226172201502.png)

**==srst命令==**

srst：重置server状态

```shell
echo srst | nc localhost 2181
```

**==wchs命令==**

wchs: 列出服务器watches的简洁信息

```shell
echo wchs | nc localhost 2181
```

![image-20201226172703565](images/image-20201226172703565.png)

**==wchc==**

wchc: 通过session分组，列出watch的所有节点，它的输出是一个与watch相关的会话的节点列表

![image-20201226172909618](images/image-20201226172909618.png)

**==wchp命令==**

wchp： 通过路径分组，列出所有的watch 的session信息

![image-20201226173329215](images/image-20201226173329215.png)

**==mntr命令==**

mntr: 列出服务器的健康状态

![image-20201226173449435](images/image-20201226173449435.png)

![image-20201226173535386](images/image-20201226173535386.png)

**思维导图：**https://www.processon.com/view/link/5b46f930e4b07b023103bcaf#map

### 分布式锁



<img src="images/image-20201229210225844.png" alt="image-20201229210225844" style="zoom:80%;" />

- InterProcessMutex：分布式可重入排它锁
- InterProcessSemaphoreMutex：分布式排它锁
- InterProcessReadWriteLock：分布式读写锁
- InterProcessMultiLock：将多个锁作为单个实体管理的容器

#### **InterProcessMutex**

##### 获取锁过程

**一，实例化InterProcessMutex**

```java
// 代码进入：InterProcessMutex.java
    /**
     * @param client client
     * @param path   the path to lock
     */
    public InterProcessMutex(CuratorFramework client, String path)
    {
        this(client, path, new StandardLockInternalsDriver());
    }
    /**
     * @param client client
     * @param path   the path to lock
     * @param driver lock driver
     */
    public InterProcessMutex(CuratorFramework client, String path, LockInternalsDriver driver)
    {
        this(client, path, LOCK_NAME, 1, driver);
    }
```

> 两个构造函数共同的入参：
>
> - client：curator实现的zookeeper客户端
> - path：要在zookeeper加锁的路径，即后面创建临时节点的父节点

==上面的两个构造函数，其实也是第一个在调用第二个，传入默认的变量==

------

```dart
// 代码进入：InterProcessMutex.java
InterProcessMutex(CuratorFramework client, String path, String lockName, int maxLeases, LockInternalsDriver driver)
    {
        basePath = PathUtils.validatePath(path);
        internals = new LockInternals(client, driver, path, lockName, maxLeases);
    }
// 代码进入：LockInternals.java
LockInternals(CuratorFramework client, LockInternalsDriver driver, String path, String lockName, int maxLeases)
    {
        this.driver = driver;
        this.lockName = lockName;
        this.maxLeases = maxLeases;

        this.client = client.newWatcherRemoveCuratorFramework();
        this.basePath = PathUtils.validatePath(path);
        this.path = ZKPaths.makePath(path, lockName);
    }
```

以上代码主要做了两件事：

1. 验证入参path的合法性	
2. 实例化一个LockInternals对象

**二，加锁方法acquire**

> 实例化完成的InterProcessMutex对象，开始调用acquire() 方法来尝试加锁

```java
// 代码进入：InterProcessMutex.java
   /**
     * Acquire the mutex - blocking until it's available. Note: the same thread
     * can call acquire re-entrantly. Each call to acquire must be balanced by a call
     * to {@link #release()}
     *
     * @throws Exception ZK errors, connection interruptions
     */
    @Override
    public void acquire() throws Exception
    {
        if ( !internalLock(-1, null) )
        {
            throw new IOException("Lost connection while trying to acquire lock: " + basePath);
        }
    }

    /**
     * Acquire the mutex - blocks until it's available or the given time expires. Note: the same thread
     * can call acquire re-entrantly. Each call to acquire that returns true must be balanced by a call
     * to {@link #release()}
     *
     * @param time time to wait
     * @param unit time unit
     * @return true if the mutex was acquired, false if not
     * @throws Exception ZK errors, connection interruptions
     */
    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception
    {
        return internalLock(time, unit);
    }
```

- acquire() : 入参为空，调用该方法后，会一直阻塞，直到抢占到锁资源，或者zookeeper链接中断后，上抛异常。
- acquire(long time, TimeUnit unit)：入参传入超时时间以及单位，抢夺时，如果出现堵塞，会在超过该时间后，返回false。

对比两种方式，可以选择适合自己业务逻辑的方法。但是一般情况下，我推荐后者，传入超时时间，避免出现大量的临时节点累积以及线程堵塞的问题。

**三，锁的可重人**

```java
// 代码进入：InterProcessMutex.java
private boolean internalLock(long time, TimeUnit unit) throws Exception
    {
        /*
           Note on concurrency: a given lockData instance
           can be only acted on by a single thread so locking isn't necessary
        */

        Thread currentThread = Thread.currentThread();

        LockData lockData = threadData.get(currentThread);
        if ( lockData != null )
        {
            // re-entering
            lockData.lockCount.incrementAndGet();
            return true;
        }
        String lockPath = internals.attemptLock(time, unit, getLockNodeBytes());
        if ( lockPath != null )
        {
            LockData newLockData = new LockData(currentThread, lockPath);
            threadData.put(currentThread, newLockData);
            return true;
        }
        return false;
    }
```

这段代码实现了锁的可重入，每个InterProcessMutex实例，都会持有一个Concurrentmap类型的threadData对象，以线程对象作为key，以LocalData作为值、通过判断当前线程threadData是否有值，如果有则表示当前线程可重入，于是将lockCount进行累加；如果没有，则进行锁的抢夺。

当internals.attemptLock方法返回lockPath 等于 null时，表明了该线程已经成功持有了这把锁，于是乎LockData对象被new了出来，并存放到threadData中。

**四，抢夺锁**

> attemptLock方法就是核心部分，直接看代码

```java
// 代码进入：LockInternals.java
String attemptLock(long time, TimeUnit unit, byte[] lockNodeBytes) throws Exception
    {
        final long      startMillis = System.currentTimeMillis();
        final Long      millisToWait = (unit != null) ? unit.toMillis(time) : null;
        final byte[]    localLockNodeBytes = (revocable.get() != null) ? new byte[0] : lockNodeBytes;
        int             retryCount = 0;

        String          ourPath = null;
        boolean         hasTheLock = false;
        boolean         isDone = false;
        while ( !isDone )
        {
            isDone = true;

            try
            {
                ourPath = driver.createsTheLock(client, path, localLockNodeBytes);
                hasTheLock = internalLockLoop(startMillis, millisToWait, ourPath);
            }
            catch ( KeeperException.NoNodeException e )
            {
                // gets thrown by StandardLockInternalsDriver when it can't find the lock node
                // this can happen when the session expires, etc. So, if the retry allows, just try it all again
                if ( client.getZookeeperClient().getRetryPolicy().allowRetry(retryCount++, System.currentTimeMillis() - startMillis, RetryLoop.getDefaultRetrySleeper()) )
                {
                    isDone = false;
                }
                else
                {
                    throw e;
                }
            }
        }

        if ( hasTheLock )
        {
            return ourPath;
        }
        return null;
    }
```

此处注意三个地方

- while循环
  - 正常情况下，这个循环会在下一次结束。但是当出现NoNodeException异常时，会根据zookeeper客户端的重试策略，进行有限次数的重新获取锁
- driver.createTheLock
  - 顾名思义，这个driver的createTheLock方法就是在创建这个锁，即在zookeeper的指定路径上，创建一个临时有序节点。注意：此时只是纯粹的创建了一个节点，不是说线程已经持有锁。

```java
// 代码进入：StandardLockInternalsDriver.java
    @Override
    public String createsTheLock(CuratorFramework client, String path, byte[] lockNodeBytes) throws Exception
    {
        String ourPath;
        if ( lockNodeBytes != null )
        {
            ourPath = client.create().creatingParentContainersIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, lockNodeBytes);
        }
        else
        {
            ourPath = client.create().creatingParentContainersIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path);
        }
        return ourPath;
    }
```

- 判断自身能否持有锁。如果不能，进入wait，等待被唤醒。

```java
// 代码进入：LockInternals.java
private boolean internalLockLoop(long startMillis, Long millisToWait, String ourPath) throws Exception
    {
        boolean     haveTheLock = false;
        boolean     doDelete = false;
        try
        {
            if ( revocable.get() != null )
            {
                client.getData().usingWatcher(revocableWatcher).forPath(ourPath);
            }

            while ( (client.getState() == CuratorFrameworkState.STARTED) && !haveTheLock )
            {
                List<String>        children = getSortedChildren();
                String              sequenceNodeName = ourPath.substring(basePath.length() + 1); // +1 to include the slash

                PredicateResults    predicateResults = driver.getsTheLock(client, children, sequenceNodeName, maxLeases);
                if ( predicateResults.getsTheLock() )
                {
                    haveTheLock = true;
                }
                else
                {
                    String  previousSequencePath = basePath + "/" + predicateResults.getPathToWatch();

                    synchronized(this)
                    {
                        try 
                        {
                            // use getData() instead of exists() to avoid leaving unneeded watchers which is a type of resource leak
                            client.getData().usingWatcher(watcher).forPath(previousSequencePath);
                            if ( millisToWait != null )
                            {
                                millisToWait -= (System.currentTimeMillis() - startMillis);
                                startMillis = System.currentTimeMillis();
                                if ( millisToWait <= 0 )
                                {
                                    doDelete = true;    // timed out - delete our node
                                    break;
                                }

                                wait(millisToWait);
                            }
                            else
                            {
                                wait();
                            }
                        }
                        catch ( KeeperException.NoNodeException e ) 
                        {
                            // it has been deleted (i.e. lock released). Try to acquire again
                        }
                    }
                }
            }
        }
        catch ( Exception e )
        {
            ThreadUtils.checkInterrupted(e);
            doDelete = true;
            throw e;
        }
        finally
        {
            if ( doDelete )
            {
                deleteOurPath(ourPath);
            }
        }
        return haveTheLock;
    }
```

> - while循环
>   - 如果一开始使用acquire方法，那么此处的循环可能就是一个死循环。当zookeeper客户端启动时，并且当线程还没有成功获取锁的时，就会开始新的一轮循环。
> - getSortedChildren
>   - 这个方法比较简单，就是获取到所有子节点列表，并且从小到大根据节点名称后10位数字进行排序，上面提到了，创建的是顺序节点
> - driver.getsTheLock
>
> ```java
> // 代码进入：StandardLockInternalsDriver.java
> @Override
>     public PredicateResults getsTheLock(CuratorFramework client, List<String> children, String sequenceNodeName, int maxLeases) throws Exception
>     {
>         int             ourIndex = children.indexOf(sequenceNodeName);
>         validateOurIndex(sequenceNodeName, ourIndex);
> 
>         boolean         getsTheLock = ourIndex < maxLeases;
>         String          pathToWatch = getsTheLock ? null : children.get(ourIndex - maxLeases);
> 
>         return new PredicateResults(pathToWatch, getsTheLock);
>     }
> ```
>
> 判断是否可以持有锁，判断规则：当前创建的节点是否在上一步获取到的子节点列表的首位。
>
> 如果是，说明可以持有锁，那么getTheLock =true,封装进PredicateResults 返回。
>
> 如果不是，说明有其他线程早已先持有了锁。那么getsTheLock = false, 此处还需要获取到自己前一个临时节点的名称pathToWatch。(注意这个pathToWatch后面有比较关键的作用)

**sychronized(this)**

这块代码在争夺失败以后的逻辑中。那么此处该线程应该做什么呢？

> 首先添加一个watcher监听，而监听的地址正是上面一步返回的pathToWatch进行basePath + "/" 拼接以后的地址。也就是说当前线程会监听自己前一个节点的变动，而不是父节点下所有节点的变动。然后华丽丽的...wait(millisToWait)。线程交出cpu的占用，进入等待状态，等到被唤醒。
>
> 接下来的逻辑就很自然了，如果自己监听的节点发生了变动，那么就将线程从等待状态唤醒，重新一轮的锁的争夺。

此时我们就完成了整个锁的抢夺过程。

##### 释放锁

> 释放锁的逻辑相对简单

```csharp
// 代码进入：InterProcessMutex.java
/**
     * Perform one release of the mutex if the calling thread is the same thread that acquired it. If the
     * thread had made multiple calls to acquire, the mutex will still be held when this method returns.
     *
     * @throws Exception ZK errors, interruptions, current thread does not own the lock
     */
    @Override
    public void release() throws Exception
    {
        /*
            Note on concurrency: a given lockData instance
            can be only acted on by a single thread so locking isn't necessary
         */

        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);
        if ( lockData == null )
        {
            throw new IllegalMonitorStateException("You do not own the lock: " + basePath);
        }

        int newLockCount = lockData.lockCount.decrementAndGet();
        if ( newLockCount > 0 )
        {
            return;
        }
        if ( newLockCount < 0 )
        {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + basePath);
        }
        try
        {
            internals.releaseLock(lockData.lockPath);
        }
        finally
        {
            threadData.remove(currentThread);
        }
    }
```

- 减少重入锁的计数，直到变成0
- 释放锁，即移除Watcher & 删除创建的节点
- 从threadData中，删除自己线程的缓存

##### 驱动类

> 开始的时候，我们提到了这个StandardLockInternalsDriver 标准锁驱动类。还提到了我们可以传入自定义的，来扩展。
>
> 先看看它提供的功能接口

```java
// 代码进入LockInternalsDriver.java
public PredicateResults getsTheLock(CuratorFramework client, List<String> children, String sequenceNodeName, int maxLeases) throws Exception;

public String createsTheLock(CuratorFramework client,  String path, byte[] lockNodeBytes) throws Exception;

// 代码进入LockInternalsSorter.java
public String           fixForSorting(String str, String lockName);
```

- getsTheLock:  判断是够获取到了锁
- createsTheLock: 在zookeeper的指定路径上，创建一个临时序列节点
- fixForSorting: 修复排序，在StandardLockInternalsDriver的实现中，即获取到临时节点的最后序列数，进行排序。

借助于这个类，我们可以尝试实现自己的锁机制，比如判断获得的策略可以做修改，比如获取子节点列表的排序方案可以自定义。。。

##### 总结

InterProcessMutex 通过在zookeeper的某路径节点下创建临时有序节点来实现分布式锁，即每个线程（跨进程的线程）获取同一把锁前，都需要在同样的路径下创建一个节点，节点名字由uuid + 递增序列组成。而通过对比自身的序列数是否在所有子节点的第一位，来判断是否成功获取到了锁。当获取锁失败时，它会添加watcher来监听前一个节点的情况，然后进行等待状态。直到watcher的事件生效将自己唤醒，或者超时时间异常返回。



## 网络编程

### 网络通信的三要素

**IP**

> 网路中计算机的唯一表标识：
>
> ​	32bit（4字节），一般用于“点分十进制”表示，如192.168.1.158；
>
> ​	IP地址 = 网络地址 + 主机地址 可分类：
>
> - A类：第1个8位表示网络地址。剩下的3个8位表示主机地址
> - B类：前2个8位表示网络地址。剩下的2个8位表示主机地址
> - C类：前3个8位表示网络地址。剩下的1个8位表示主机地址
> - D类地址用于在IP网络中组播
> - E类地址保留做科研之用

Java编程中可以通过InetAddress类去获取IP地址

```java
InetAddress localHost = InetAddress.getLocalHost();
localHost.getLocalHost();
localHost.getHostName();
```

**端口号**

用于标识进程的逻辑地址，不同进程的标识。

有效端口：0-65535，其中0-1024 系统使用或保留端口

**传输的协议**

- **UDP**
  - 将数据和目标封装成数据包中，不需要建立连接
  - 每个数据报大小限制在64k以内
  - 因为不需要建立连接，是不可靠的协议
  - 不需要建立连接速度快
- **TCP**
  - 建立连接，形成通信通道
  - 在连接中进行大量数据传输
  - 通过三次握手完成连接，是可靠协议
  - 必须建立连接，效率会稍低

### 网络模型

<img src="images/image-20201228135646145.png" alt="image-20201228135646145" style="zoom:80%;" />

**物理层**

​	主要定义物理设备标准，如网线的接口类型，光纤的接口类型，各种传输介质的传输速率等。

​	主要作用是将数据最终编码为0,1表示的比特流，通过物理介质传输。这一层叫做比特

**数据链路层**

​	主要将接受到的数据进程MAC地址否封装与解封装。常把这一层的数据叫做帧。这一层常工作的设备是交换机

**网络层**

​	主要讲收到的数据进行IP地址的封装与解封，常把这一层叫做数据包。这一层的设备是路由器

**传输层**

​	定义了一些数据传输的协议和端口号。

​	主要将接受的数据进行分段和传输，到达目的地址后在进行重组。

​	常把这一层的数据叫做段。

**会话层**

​	通过传输层建立数据传输的通路。主要在系统之间发起会话或者接收会话请求。

**表示层**

​	主要进行对接受数据的解释，加密与解密，压缩与解压缩

​	确保一个系统的应用层发送的数据能被另一个系统的应用层识别。

**应用层**

​	主要是为了一些终端应用程序提供服务。直接面对着用户。



### IO通信模型



### Socket机制





### RPC原理























