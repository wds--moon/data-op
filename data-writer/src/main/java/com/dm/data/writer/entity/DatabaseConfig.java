package com.dm.data.writer.entity;

import lombok.Data;

/**
 * 数据库连接配置
 * @author wendongshan
 */
@Data
public class DatabaseConfig {
    private Integer id;
    private String url;
    private String databaseName;
    private String pwd;
    private String username;
}
