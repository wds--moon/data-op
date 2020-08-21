package com.dm.data.writer.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * 配置一个主数据源,其他数据源动态加载
 *
 * @author wendongshan
 */
@Configuration
public class DynamicLoadDataSource {

    @Bean
    @Qualifier("testWriteDataSource")
    @ConfigurationProperties("spring.datasource.testwrite")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "testWriteJdbcTemplate")
    public NamedParameterJdbcTemplate testWriteJdbcTemplate( @Qualifier("testWriteDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    @DependsOn({"springUtils", "defaultDataSource"})
    public DynamicDataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(DynamicDataSource.dataSourcesMap);
        return dynamicDataSource;
    }

}
