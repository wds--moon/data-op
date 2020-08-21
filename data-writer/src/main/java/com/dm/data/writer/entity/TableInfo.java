package com.dm.data.writer.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 数据库配置接口和表信息
 *
 * @author wendongshan
 */
public class TableInfo {
    private Long id;
    private String interfaceName;
    private String tableName;
    private String database;

    public TableInfo() {
    }

    public TableInfo(Long id, String interfaceName, String tableName, String database) {
        this.id = id;
        this.interfaceName = interfaceName;
        this.tableName = tableName;
        this.database = database;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TableInfo tableInfo = (TableInfo) o;

        return new EqualsBuilder()
                .append(id, tableInfo.id)
                .append(interfaceName, tableInfo.interfaceName)
                .append(tableName, tableInfo.tableName)
                .append(database, tableInfo.database)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(interfaceName)
                .append(tableName)
                .append(database)
                .toHashCode();
    }
}
