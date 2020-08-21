package com.dm.data.writer.component;

import com.dm.data.writer.config.DynamicDataSource;
import com.dm.data.writer.entity.DatabaseConfig;
import com.dm.data.writer.entity.TableInfo;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @author wendongshan
 */
@Component
public class DynamicJdbcComponent {

    @Autowired
    @Qualifier("testWriteJdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * 根据数据库名字加载对应的数据库连接信息
     *
     * @param database
     * @return
     */
    public DatabaseConfig findByDatabase(String database) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("databaseName", database);
        String sql = "select * from database_config where databaseName= :databaseName";
        RowMapper<DatabaseConfig> rm = BeanPropertyRowMapper.newInstance(DatabaseConfig.class);
        return jdbcTemplate.queryForObject(sql, map, rm);
    }

    /**
     * 查询指定的一条数据
     *
     * @param interfaceName
     * @return
     */
    public TableInfo findByInterfaceName(String interfaceName) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("interfaceName", interfaceName);
        String sql = "select * from configure where interfaceName= :interfaceName";
        RowMapper<TableInfo> rm = BeanPropertyRowMapper.newInstance(TableInfo.class);
        return jdbcTemplate.queryForObject(sql, map, rm);
    }

    /**
     * 重新设置连接
     *
     * @param databaseConfig
     */
    public void loadDataSource(DatabaseConfig databaseConfig) {
        /**
         * 如果缓存中没有数据源信息,就重新加载
         */
        if (DynamicDataSource.dataSourcesMap.get(databaseConfig.getDatabaseName()) == null) {
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setJdbcUrl(databaseConfig.getUrl());
            hikariDataSource.setUsername(databaseConfig.getUsername());
            hikariDataSource.setPassword(databaseConfig.getPwd());
            DynamicDataSource.dataSourcesMap.put(databaseConfig.getDatabaseName(), hikariDataSource);
        }
        /**
         * 指定切换数据源
         */
        DynamicDataSource.setDataSource(databaseConfig.getDatabaseName());
    }
}
