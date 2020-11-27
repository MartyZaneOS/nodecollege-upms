# 通用权限管理系统

此系统最初是用于写着玩，积累技术的，后来调到公司用户中心专门做这块，学到了很多，把自己的一些理解和想法，慢慢完善后将其通用的权限管理模块开源出来，也是希望能够互相交流学习，共同进步，我也会将这个系统的一些开发经验与技巧写出来，分享给大家。然后，作为一名后端工程师兼审美不太好，前端写的不是很好很美观，请多多包涵。

此系统后端基于Spring Boot2，前端基于vue，包含三个用户群体（管理员、用户、租户成员）的权限管理，以及一些基础和便于项目维护迭代的功能。

特点：

1. 通过微服务接口管理、前端工程管理、产品管理三个基本功能，能够一目了然的知道系统一共包含多少个后端接口，有几个前端工程，有几个前端页面，以及产品菜单树的构成。适用于有大量页面功能，大量历史接口、开发人员经常变更、产品多而杂、项目周期长的项目。
2. 基于SAAS的定位，同时拥有三个维度的用户管理体系，实现B端多租户管理。
3. 方便的接口访问权限控制，和便于进行接口数据权限的校验。



## 演示地址

除了权限管理的功能，我还在此基础上自己写了一套C端用户的聊天系统和博客系统。博客系统还不完善，代码还没放出来。租的阿里云的服务器，内存有限，就拿正式环境开放出来测试了，下面的运维运营只给了查看权限，没有操作权限。更多的功能建议自己下载代码运行后试试吧。

- 首页地址：http://www.nodecollege.com
- 运维运营登录地址：http://www.nodecollege.com/admin/login    账号/密码：`admin1 `/`123456aA.`
- 用户登录地址：http://www.nodecollege.com/me/login     账号/密码： `15739575703`/`123456aA.`
- 租户登录地址：
http://test01.nodecollege.com/tenant/login    账号/密码：`00001`/`a123456`
http://test02.nodecollege.com/tenant/login    账号/密码：`00001`/`aA123456`
- Gitee地址：https://gitee.com/tinyLC/nodecollege-upms 
- Github地址：https://github.com/tinylc/nodecollege-upms

**如果觉得不错的话，可以给我一个start嘛？**

**文件结构**

- server：后端工程
  - nacos：nacos服务
  - tools：通用工具包
  - operate：运营/用户后端
  - gateway：接口网关
  - log：日志后端
  - sync：定时任务后端
  - chat：聊天室后端
  - tenant：租户管理后端
- ui：前端工程
  - common：前端公用组件
  - operate：运营/运维前端
  - me：个人前端
  - tenant：租户权限管理前端
- nginx：nginx配置文件
- sql: 初始化脚本


## 一、介绍
企业级多租户后台权限管理框架（SAAS）。


![技术架构图.jpg](http://www.nodecollege.com/ncimg/M00/00/02/rBIgzV-r0k-AQsqqAAIWVFU8yDk93.jpeg)


## 二、主要功能：

### 1 后端微服务接口管理->前端工程管理->产品管理

这三个管理功能是依次依赖的，为后期的权限管理提供基础数据，同时也是便于清晰直白的体现整个系统的功能，利于后期系统的维护。

**后端微服务接口管理**

两个主要的功能：

1）用于管理查看所有的微服务接口信息，用于后期服务增多，接口增多后，进行系统优化时提供参考。

2）进行接口访问权限的设置。

微服务工程只需要引入`base-client`包，就可以自动添加、更新该微服务所有的接口信息，无需手动操作。在产品管理里进行配置了后，通过**刷新产品授权限制信息**按钮就可以自动完成接口访问授权的设置了。

TODO: 接口文档自动生成，接口自动化测试，接口访问量统计。

![微服务接口管理.png](http://www.nodecollege.com/ncimg/M00/00/03/rBIgzV-r0nSARIdBAAFEFJLg5YY591.png)

**前端工程管理**

主要作用：清晰直观的体现系统一共有哪些前端工程，每个前端工程有几个页面，每个页面可以调用哪些接口。

前端和页面数据由前端开发人员将各自前端功能开发完成后，手动进行录入，接口数据从后端服务接口中手动选择。

![前端工程管理.png](http://www.nodecollege.com/ncimg/M00/00/03/rBIgzV-r0qCAD77IAAENCD3_jBY512.png)



**产品管理**

产品是面向客户群体的，系统功能是围绕产品进行开发的，所以在这里进行产品的设置和定义，同时将对应的系统功能进行绑定。

产品绑定的菜单中，分类导航菜单需要手动进行创建，菜单页面从前端工程管理里选取。

![产品管理.png](http://www.nodecollege.com/ncimg/M00/00/03/rBIgzV-r0rCAQzINAAEEL7HsVX4186.png)

### 2 产品管理

[产品管理详细说明](http://www.nodecollege.com/article/info/35)




### 3 角色管理

[角色管理详细说明](http://www.nodecollege.com/article/info/36)



### 4 运营运维、用户、租户三个用户群体的权限管理，数据隔离

三个用户群体的机构、用户、角色单独进行管理。

租户属于`2B`业务，租户之间数据隔离，可以为不同的租户开通不同的`2B`产品。

**TODO:** 租户数据库级别的数据隔离；租户开通产品访问量统计；访问量阈值设定。



### 5 细粒度接口权限控制

接口调用权限：无限制、只能内部调用、管理员登录|授权访问，用户登录|授权访问，租户成员登录|授权访问。

接口数据权限：所有机构数据、所属机构及下级机构数据、所属机构数据、个人数据

[接口权限控制详细说明](http://www.nodecollege.com/article/info/37)



### [6 其他功能说明](http://www.nodecollege.com/article/info/34)



## 三、模块化设计与使用

业务概述![业务概述.jpg](http://www.nodecollege.com/ncimg/M00/00/02/rBIgzV-r0g6AREBqAAGZinqlZ8o74.jpeg)

服务对应功能

| 前后端服务                                             | 对应功能                                                     | 是否开源 |
| ------------------------------------------------------ | ------------------------------------------------------------ | -------- |
| /server/operate<br />/server/gateway<br />/ui/operate  | 管理员权限管理<br />微服务接口管理<br />前端管理<br />产品管理<br />网关管理<br />配置管理<br />文件管理 | 是       |
| /server/log<br />/server/sync                          | 系统日志管理<br />任务管理<br />任务日志管理                 | 是       |
| /ui/me                                                 | 用户权限管理                                                 | 是       |
| /server/chat                                           | 聊天系统<br />通讯录                                         | 是       |
| /server/tenant<br />/ui/tenant                         | 租户权限管理                                                 | 是       |
| /server/worldTree<br />/ui/web                         | 世界树和辩论堂相关功能                                       | 否       |
| /server/article<br />/ui/article<br />/ui/article-info | 文章相关功能                                                 | 否       |

/server/nacos、operate、gateway和/me/operate是基础微服务，只运行这四个服务，即可拥有运维运营及管理员主要功能。

/server/log、sync这两个后端服务，提供日志和任务调度功能，**不启动**这两个微服务的情况下，请确保其他服务配置文件中日志推送标志**不为true**（默认为false） **log.posh=false** ，为true的情况下，每个微服务都会将各自的日志信息存储到redis中，然后靠sync和log服务从redis中取出进行消费。

/ui/me，运行这个前端服务，即可使用C端用户功能。

/server/chat，运行这个后端服务，C端用户即可使用聊天和通讯录功能，前端在/ui/me中

/server/tenant，/ui/tenant，运行这两个服务后，可以使用租户管理相关功能。租户功能除了依赖之前的基础服务外，还依赖C端用户的功能，因为租户成员账号是 C端用户的一个身份，一个C端用户可以有多个租户成员账号。

**注**：当然要使用上述的功能，还得开通相应的权限才行


## 四、技术技巧

[密码RSA前端加密，后端解密](http://www.nodecollege.com/article/info/38)

[vue、nuxt添加百度统计功能](http://www.nodecollege.com/article/info/44)

## 五、运行和部署

server和ui文件内的工程都是独立的，正规公司都是每个工程都单独一个git工程，我这为了方便放一个git里了。

1. 创建数据库、导入数据

2. 启动redis、nacos

   ```
   # redis自行启动，nacos在/server/nacos/bin/ 目录中有配套的启动文件 
   # 注意linux下单机启动命令如下
   sh startup.sh -m standalone
   ```

3. 配置启动/server/operate、gateway

   operate是基础核心服务，gateway是接口网关，所有前端接口调用都走gateway，gateway是动态网关，网关信息从redis中的获取（operate启动时将网关信息从数据库读取然后存储到redis中）。

4. 启动/ui/operate

   ```nodejs
   # /ui/operate
   npm install
   npm run dev
   ```

   启动成功后访问 http://localhost:8081/admin/login 登录即可 运维运营用户名/密码 `admin`/`123456aA.` 此时除了日志功能、任务调用功能访问会报404，租户功能不能正常使用外，其他功能都能正常使用了。

5. 启动/ui/me

   ```
   # /ui/me
   npm install
   npm run dev
   ```

   启动成功后访问 http://localhost:8083/me/login 登录 C端用户名/密码 `15739575703`/`123456aA.`  此时处理聊天功能、租户功能外，C端功能都能使用。

6. 配置启动/server/chat

   启动成功后，C端聊天功能能够正常使用

7. 配置启动/server/log、sync

   启动成功后，运维运营端日志功能和任务调度功能能够正常使用了

8. 配置启动/server/tenant

9. 启动/ui/tenant

   启动成功后访问，http://localhost:8084/tenant/login  租户成员账号/密码  `15739575703`/`a123456` 此时，租户功能能够正常使用了。

   注意：这是多租户系统，所以每个租户的登录地址都应该是这样的 http://{租户代码}.域名.com/tenant/login，通过二级域名的方式进行登录，然后将二级域名作为一个参数传递给后端服务，用于辨识是那个租户，所以本地启动时，需要在/ui/tenant/config/index.js文件中，将要用于登录的租户成员账号对应的租户代码进行手动填入，模拟二级域名缺失的参数

   ```javascript
   'use strict'
   // Template version: 1.3.1
   // see http://vuejs-templates.github.io/webpack for documentation.
   
   const path = require('path')
   
   function onProxyReq (proxyReq, req, res) {
     // 本地开发时，只能使用 如下设置的 B端租户代码 对应的B端账号进行登录
     proxyReq.setHeader('LOGIN-TENANT-CODE', 'applyTenantTest13');
     proxyReq.setHeader('ACCESS-SOURCE', 'nc-nginx');
   }
   module.exports = {
     dev: {
       // Paths
       assetsSubDirectory: 'static',
       assetsPublicPath: '/',
       proxyTable: {
         '/tenantApi': {
           target: 'http://localhost:1081/',
           changeOrigin: true,
           // 代理响应事件
           onProxyReq: onProxyReq,
           pathRewrite: {
             '^/': '/'
           }
         }
       },
   
       // Various Dev Server settings
       host: 'localhost', // can be overwritten by process.env.HOST
       port: 8084, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
   ...
   }
   ```

   

## 六、结束

