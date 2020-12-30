# 中国所有学校基础信息数据

> 幼儿园、小学、中学、职业学校、大学

> 名称、省、市、区、详细地址、经纬度、类型

> 已收集到的数据为405073

> 单次执行时间为60分钟左右，其中以"幼儿园"的数据最多也最慢

## 数据来源

 - 高德地图API
 
## 依赖

 - Java 1.8及以上
 - Maven 
 - HuTool

## 使用方法

 #### 注册高德开放平台，完成认证
 
 注册地址: https://lbs.amap.com/
 
 #### 创建应用，获取App Key
 
 创建地址：https://console.amap.com/dev/key/app
 
 将Key替换`Starter`类中的`ak`变量
 
 修改`Starter`类中的`outputPath`变量为自己的可读写路径
 
 #### 执行代码
 
 执行`Starter`类的`main`方法，开始数据爬取。
 
 考虑到高德的API调用限额，本程序仅使用了单线程，并且每次接口请求后会挂起线程20毫秒，代码位置在`SchoolSpider`的122行：
 
```
// 挂起20毫秒，保证调用频率不超过限额
ThreadUtil.sleep(20);
```

 另外提供了`DbRunner`类，用来将生成的json文件转储到数据库中。数据库的配置文件在`resources/db.setting`
 
 有二次开发能力的小伙伴也可以修改`SchoolSpider`，将数据直接存储到数据库中。