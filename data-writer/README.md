**要求所有的接口必须提供long id唯一标示传递过来**

* 需要处理的逻辑
* 采集日志的记录
* 数据的批量写入
* 异常处理
* 数据包的定义
* 不同数据类型的传输和写入标准定义
    * boolean 型的传输和标准定义
    * 日期类型的传输和定义
* 存在问题
    * 数据唯一性校验
    * 批量数据是否支持回滚,是否支持部分成功，
    * 数据实时性
    * 消息丢失是否需要双方保证
    * 接口幂等性

    只处理数据新增和更新，不处理删除