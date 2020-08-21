package com.dm.data.writer.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wendongshan
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> DATASOURCE_KEY = ThreadLocal.withInitial(() -> "defaultDataSource");

    public static Map<Object, Object> dataSourcesMap = new ConcurrentHashMap<>(10);

    static {
        dataSourcesMap.put("defaultDataSource", SpringUtils.getBean("defaultDataSource"));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSource.DATASOURCE_KEY.get();
    }

    public static void setDataSource(String dataSource) {
        DynamicDataSource.DATASOURCE_KEY.set(dataSource);
        DynamicDataSource dynamicDataSource = (DynamicDataSource) SpringUtils.getBean("dataSource");
        dynamicDataSource.afterPropertiesSet();
    }

    public static String getDataSource() {
        return DynamicDataSource.DATASOURCE_KEY.get();
    }

    public static void clear() {
        DynamicDataSource.DATASOURCE_KEY.remove();
    }
}
