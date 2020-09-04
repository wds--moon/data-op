package com.dm.data.writer.component;


import com.dm.data.writer.dto.ImgThread;
import com.dm.data.writer.entity.TableInfo;
import com.dm.data.writer.util.DataModel;
import com.dm.data.writer.util.ImageBase64Utils;
import com.dm.data.writer.util.TableEnum;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wendongshan
 */
@Slf4j
@Component
public class JdbcComponent {

    private static final String ID = "id";
    private static final String IMG = "img";

    @Value("${base.img.url}")
    private String imgUrl;

    @Autowired
    private DynamicJdbcAdapter jdbcAdapter;

    /**
     *
     * 由于是io操作 设置核心线程数量1 最大线程数量cpu核数*8  设置队列1 设置线程名称为img-pool-   异常机制使用抛出异常
     *
     */
    static ExecutorService executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(1), new ThreadFactoryBuilder().setNameFormat("img-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    @Transactional(rollbackFor = Exception.class)
    public void save(DataModel model, TableInfo tableName) {
        String sql;
        for (LinkedHashMap<String, Object> data : model.getData()) {
            setImg(data);
            if (generatorSelectSql(tableName, Long.parseLong(data.get(ID).toString()))) {
                sql = generatorUpdateSql(tableName.getTableName(), data);
            } else {
                sql = generatorInsertSql(tableName.getTableName(), data);
            }
            jdbcAdapter.update(tableName, sql, data);
        }
    }

    private void setImg(LinkedHashMap<String, Object> data) {
        /**
         * 如果存在img图片,那么需要把图片转存到本地,本地存储指定的相对url地址,图片转换失败继续操作
         */

        if (data.containsKey(IMG) && data.get(IMG) != null) {
            List<String> imgs = (List<String>) data.get(IMG);
            List<String> list = new ArrayList<>();
            for (String img : imgs) {
                String fileName = UUID.randomUUID().toString() + ImageBase64Utils.getImgSuffix(img);
                String url = imgUrl + File.separator + fileName;
                list.add(fileName);
                executorService.submit(new ImgThread(url, img));
            }

            data.put("img", StringUtils.join(list, ","));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setSaveLog(String interfaceName, String data, String status, String remark, Integer num, String databaseName, String tableName) {

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
