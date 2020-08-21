package com.dm.data.writer.util;


/**
 * 枚举类型,提供接口到表的转换,并且提供表的数据库名称从而指定不同数据源
 * @author wendongshan
 */
public enum TableEnum {
    /**
     * 日志指定路径
     */
    INTERFACE_LOG("log", "interface_log","test_writer");
    /**
     * 接口名称
     */
    private String interfaceName;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 数据库
     */
    private String database;

    /**
     * 构造函数
     * @param interfaceName 接口名称
     * @param tableName 表名称
     */
    TableEnum( String interfaceName, String tableName,String database) {
        this.interfaceName = interfaceName;
        this.tableName = tableName;
        this.database=database;
    }

    /**
     * 通过接口获取表名称
     * @param interfaceName
     * @return
     */
   public static TableEnum getTableName(String interfaceName){
       for (TableEnum tableEnum : values()) {
           if (tableEnum.getInterfaceName().equals(interfaceName)) {
               return  tableEnum;
           }
       }
       return null;
    }

    /**
     * 通过表名称获取数据库名称
     * @param tableName
     * @return
     */
    public static TableEnum findDatabase(String tableName){
        for (TableEnum tableEnum : values()) {
            if (tableEnum.getTableName().equals(tableName)) {
                return  tableEnum;
            }
        }
        return null;
    }


    public String getInterfaceName() {
        return interfaceName;
    }


    public String getTableName() {
        return tableName;
    }

    public String getDatabase() {
        return database;
    }
}
