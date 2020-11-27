# 通用工具
### 介绍
common-tools包将一些通用的工具类和实体进行了集成，用于提供给其他各个模块使用

想要达成的目标
1. 规范日志输出，全链路请求id，便于查询日志
    - aop.NcAop
    - config.FeignRequestInterceptor
    - logback-spring.xml
2. 定义统一返回实体、统一常用查询实体，
    - model
3. 全局异常处理
    - exception
4. 通用错误枚举，通用常数
    - enums.*
    - NcConstants.*
5. 统一redis实现工具类
6. 常用工具类
    - utils.*
7. 常用导入导出工具类