package com.dm.data.writer.component;

import com.dm.data.writer.config.DynamicDataSource;
import com.dm.data.writer.entity.InterfaceLog;
import com.dm.data.writer.entity.LogDto;
import com.dm.data.writer.entity.TableInfo;
import com.dm.data.writer.util.TableEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wendongshan
 */
@Component
public class DynamicJdbcAdapter {

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Autowired
    @Qualifier("testWriteJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 修改方法
     *
     * @param tableName 表名称
     * @param sql       sql语句
     * @param data      数据
     */
    public void update(TableInfo tableName, String sql, LinkedHashMap<String, Object> data) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dynamicDataSource);
        jdbcTemplate.update(sql, data);
    }


    /**
     * 查询数据是否存在
     *
     * @param tableName 表名称
     * @param sql       sql语句
     * @return
     */
    public boolean findById(TableInfo tableName, String sql, LinkedHashMap<String, Object> map) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dynamicDataSource);
        Integer flag = jdbcTemplate.queryForObject(sql, map, Integer.class);
        return flag > 0;
    }

    /**
     * 日志只能存储在主库里面
     *
     * @param tableName
     * @param sql
     * @param data
     */
    public void updatelog(TableInfo tableName, String sql, LinkedHashMap<String, Object> data) {
        /**
         * 动态执行完成以后重新加载主库保存日志
         */
        namedParameterJdbcTemplate.update(sql, data);
    }

    /**
     * 日志分页查询列表
     *
     * @param log
     * @param pageable
     */
    public Page<InterfaceLog> findLog(InterfaceLog log, Pageable pageable) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> map = new HashMap<>(4);
        sb.append(" select * from ").append(TableEnum.INTERFACE_LOG.getTableName()).append(" where name !='system error'  ");
        if (log != null) {
            if (StringUtils.isNotEmpty(log.getName())) {
                sb.append(" and name = :name");
                map.put("name", log.getName());
            }
            if (log.getStartDate() != null) {
                sb.append(" and create_date >= :startDate");
                map.put("startDate", log.getStartDate());
            }
            if (log.getEndDate() != null) {
                sb.append(" and  create_date <= :endDate");
                map.put("endDate", log.getEndDate());
            }
        }
        sb.append(" order by create_date desc ");
        String sql = pageQuerySQL(sb.toString(), (pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber()) * pageable.getPageSize(), pageable.getPageSize());
        RowMapper<InterfaceLog> rowMapper = BeanPropertyRowMapper.newInstance(InterfaceLog.class);
        List<InterfaceLog> query = namedParameterJdbcTemplate.query(sql, map, rowMapper);
        Integer total = namedParameterJdbcTemplate.queryForObject(sql.replace("select * from", "select count(*) from"), map, Integer.class);
        return new PageImpl(query, pageable, total);
    }

    public Page<LogDto> findGroupLog(String name, Pageable pageable) {
        String select = "conf.interfaceName as name, conf.`database` as databaseName,conf.tableName,IFNULL(sup.sum,0) as sum,IFNULL(sub.num,0) as num ,sub.create_date as createDate";
        StringBuilder sb = new StringBuilder();
        Map<String, Object> map = new HashMap<>(4);
        sb.append(" select ").append(select).append(" from ").append(" configure conf LEFT JOIN (select name,max(id) id ,COUNT(*) as sum from interface_log WHERE 1=1 and name!='system error' GROUP BY name) sup on conf.interfaceName=sup.`name` ")
        .append(" LEFT JOIN interface_log sub on sup.id=sub.id  ").append(" where 1=1 ");

        if (StringUtils.isNotEmpty(name)) {
            sb.append(" and sup.name = :name");
            map.put("name", name);
        }
        String sql = pageQuerySQL(sb.toString(), (pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber()) * pageable.getPageSize(), pageable.getPageSize());
        RowMapper<LogDto> rowMapper = BeanPropertyRowMapper.newInstance(LogDto.class);
        List<LogDto> query = namedParameterJdbcTemplate.query(sql, map, rowMapper);
        Integer total = namedParameterJdbcTemplate.queryForObject(sql.replace(select, "count(*)"), map, Integer.class);
        return new PageImpl(query, pageable, total);
    }

    /**
     * 分页查询
     *
     * @param sqlStr
     * @param startIndex
     * @param currentSize
     * @return
     */
    private String pageQuerySQL(String sqlStr, int startIndex, int currentSize) {
        StringBuffer pageBuffer = new StringBuffer();
        if (startIndex == 0 && currentSize <= 0) {
            pageBuffer.append(sqlStr);
        } else {
            pageBuffer.append(sqlStr);
            pageBuffer.append(" limit " + startIndex + "," + currentSize);
        }
        return pageBuffer.toString();
    }
}
