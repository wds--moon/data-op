package com.dm.data.writer.component;


import com.dm.data.writer.entity.TableInfo;
import com.dm.data.writer.util.DataModel;
import com.dm.data.writer.util.TableEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wendongshan
 */
@Component
public class JdbcComponent {

    private static final String ID = "id";

    @Autowired
    private DynamicJdbcAdapter jdbcAdapter;

    @Transactional(rollbackFor = Exception.class)
    public void save(DataModel model, TableInfo tableName) {
        String sql;
        for (LinkedHashMap<String, Object> data : model.getData()) {
            if (generatorSelectSql(tableName, Long.parseLong(data.get(ID).toString()))) {
                sql = generatorUpdateSql(tableName.getTableName(), data);
            } else {
                sql = generatorInsertSql(tableName.getTableName(), data);
            }
            jdbcAdapter.update(tableName, sql, data);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setSaveLog(String interfaceName, String data, String status, String remark,Integer num,String databaseName,String tableName) {

        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        param.put("name", interfaceName);
        param.put("create_date", LocalDateTime.now());
        param.put("status", status);
        param.put("data", data);
        param.put("remark", remark);
        param.put("num", num);
        param.put("databaseName", databaseName);
        param.put("tableName", tableName);
        saveLog(TableEnum.INTERFACE_LOG, param);
    }

    /**
     * 保存日志
     *
     * @param table
     * @param map
     */
    private void saveLog(TableEnum table, LinkedHashMap<String, Object> map) {
        String sql = generatorInsertSql(table.getTableName(), map);
        TableInfo tableInfo = new TableInfo(null, table.getInterfaceName(), table.getTableName(), table.getDatabase());
        jdbcAdapter.updatelog(tableInfo, sql, map);
    }

    /**
     * 新增接口
     *
     * @param table
     * @param values
     * @return
     */
    private String generatorInsertSql(String table, LinkedHashMap<String, Object> values) {
        StringBuilder builder = new StringBuilder();
        List<String> collect = values.keySet().stream().map(key -> ":" + key).collect(Collectors.toList());
        builder.append("insert into ").append(table).append("(").append(StringUtils.join(collect, ",").replace(":", "")).append(")").append(" values (")
            .append(StringUtils.join(collect, ","))
            .append(")");
        return builder.toString();
    }

    /**
     * 查询是否唯一 所有接口必须提供唯一标示id
     *
     * @param tableInfo 表信息
     * @param id        唯一id
     * @return
     */
    private boolean generatorSelectSql(TableInfo tableInfo, Long id) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select ifnull(count(*),0) from   ").append(tableInfo.getTableName()).append(" ").append(" where " + ID + "=").append(id);
        return jdbcAdapter.findById(tableInfo, sql.toString(), new LinkedHashMap<>());
    }

    /**
     * 修改存在的数据
     *
     * @param table
     * @param values
     * @return
     */
    private String generatorUpdateSql(String table, LinkedHashMap<String, Object> values) {
        StringBuilder builder = new StringBuilder();
        List<String> keys = values.keySet().stream().filter(key -> !key.equals(ID)).map(key -> key + "= :" + key).collect(Collectors.toList());
        List<String> ids = values.keySet().stream().filter(key -> key.equals(ID)).map(key -> key + "= :" + key).collect(Collectors.toList());
        builder.append("update ").append(table).append(" set ").append(StringUtils.join(keys, ",")).append(" where ").append(StringUtils.join(ids, ","));
        return builder.toString();
    }

}
