package com.dm.data.writer.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlGenerator {
    // mysql的字段分割符号是反引号;
    // sqlserver的分割符号是[]
    private static String columnSplitStart = "`";
    private static String columnSplitEnd = "`";

    /**
     * 构建insert语句
     * 
     * @param table  要构建的表
     * @param params 要构建的参数
     * @return
     */
    public static String generateInsertSql(String table, Map<String, Object> params) {
        Set<String> keys = params.keySet();
        // 获取一系列的 `key`字符串
        List<String> keyColumns = keys.stream().map(SqlGenerator::buildWrappedColumnName).collect(Collectors.toList());
        // 获取一系列的:key列表
        List<String> valueColumns = keys.stream().map(SqlGenerator::buildValueColumn).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder("insert into ");
        builder.append(table);
        builder.append(" (");
        builder.append(StringUtils.join(keyColumns, ","));
        builder.append(") values (");
        builder.append(StringUtils.join(valueColumns, ","));
        builder.append(")");
        return builder.toString();
    }

    /**
     * 将键名转换为`key`的形式
     * 
     * @param key
     * @return
     */
    private static String buildWrappedColumnName(String key) {
        return StringUtils.join(columnSplitStart, key, columnSplitEnd);
    }

    /**
     * 将键名转换为:key的形式
     * 
     * @param key
     * @return
     */
    private static String buildValueColumn(String key) {
        return StringUtils.join(":", key);
    }

    /**
     * 将一个键构建为 'key'=:key的形式
     * 
     * @param key
     * @return
     */
    private static String buildKVString(String key) {
        return StringUtils.join(buildWrappedColumnName(key), "=", buildValueColumn(key));
    }

    /**
     * 将很多键构建为'key'=:key的形式
     * 
     * @param keys
     * @return
     */
    private static List<String> buildKVString(Collection<String> keys) {
        return keys.stream().map(SqlGenerator::buildKVString).collect(Collectors.toList());
    }

    /**
     * 构建update语句
     * 
     * @param table      要更行的表
     * @param params     数据参数
     * @param keyColumns 表的id列
     * @return
     */
    public static String generateUpdateSql(String table, Map<String, Object> params, Collection<String> keyColumns) {
        Set<String> keys = params.keySet();
        StringBuilder builder = new StringBuilder("update ");
        builder.append(table).append(" set ");
        builder.append(StringUtils.join(buildKVString(keys), ","));
        builder.append(" where ");
        builder.append(StringUtils.join(buildKVString(keyColumns), " and "));
        return builder.toString();
    }

    public static void main(String[] args) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id_", "adg");
        params.put("name_", "name");
        System.out.println(generateInsertSql("user", params));
        System.out.println(generateUpdateSql("user", params, Lists.newArrayList("id_", "name_")));
    }

}
