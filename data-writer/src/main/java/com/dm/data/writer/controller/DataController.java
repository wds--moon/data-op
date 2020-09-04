package com.dm.data.writer.controller;

import com.dm.data.writer.component.DynamicJdbcAdapter;
import com.dm.data.writer.component.DynamicJdbcComponent;
import com.dm.data.writer.component.JdbcComponent;
import com.dm.data.writer.config.DynamicDataSource;
import com.dm.data.writer.entity.DatabaseConfig;
import com.dm.data.writer.entity.InterfaceLog;
import com.dm.data.writer.entity.LogDto;
import com.dm.data.writer.entity.TableInfo;
import com.dm.data.writer.util.DataModel;
import com.dm.data.writer.util.LogException;
import com.dm.data.writer.util.Result;
import com.google.common.cache.Cache;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

/**
 * @author wendongshan
 */
@Api(description = "通用数据接口")
@RestController
@RequestMapping("api/data/")
public class DataController {

    @Autowired
    private JdbcComponent jdbcComponent;
    @Autowired
    private Cache<String, Integer> cache;

    @Autowired
    private DynamicJdbcComponent dynamicJdbcComponent;

    @Autowired
    private DynamicJdbcAdapter dynamicJdbcAdapter;

    private static final String PRIMARY_DATABASE = "test_write";
    private static final Integer DATA_SIZE = 100;

    @ResponseBody
    @PostMapping("{name}")
    public Result save(@PathVariable("name") String name, @RequestBody DataModel model) throws UnsupportedEncodingException {
        if(model==null||CollectionUtils.isEmpty(model.getData())){
            throw new LogException(name, null, "数据不能为空", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (StringUtils.isEmpty(name)) {
            throw new LogException(name, model.getData(), "接口不存在", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(model.getData().size()>DATA_SIZE){
            throw new LogException(name, model.getData(), "数据集合大小一次不能超过100条", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        TableInfo tableInfo = dynamicJdbcComponent.findByInterfaceName(name);
        if (tableInfo == null) {
            throw new LogException(name, model.getData(), "接口不存在", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        model.setInterfaceName(name);
        /**
         * 指定一个主库,如果是加载主库就不加载数据,主库没有存储在数据库配置中,不管是否加载都需要重置为主库
         */
        try {
            if (!tableInfo.getDatabase().equals(PRIMARY_DATABASE)) {
                DatabaseConfig byDatabase = dynamicJdbcComponent.findByDatabase(tableInfo.getDatabase());
                dynamicJdbcComponent.loadDataSource(byDatabase);
            }
            String param = DigestUtils.md5DigestAsHex(model.toString().getBytes("UTF-8"));

            if (cache.getIfPresent(param) == null) {
                jdbcComponent.save(model, tableInfo);
                cache.put(param, 0);
            } else {
                throw new LogException(name, model.getData(), "请求过于频繁,稍后再试!", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } finally {
            DynamicDataSource.clear();
        }
        return new Result(HttpStatus.OK.value(), null, model.getInterfaceName(), HttpStatus.OK.getReasonPhrase());
    }

    @ResponseBody
    @GetMapping("logs")
    public Page<InterfaceLog> findLogList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @PageableDefault Pageable pageable) {
        InterfaceLog log = new InterfaceLog();
        if (StringUtils.isNotEmpty(name)) {
            log.setName(name);
        }
        if (startDate != null) {
            log.setStartDate(startDate);
        }
        if (endDate != null) {
            log.setEndDate(endDate);
        }
        return dynamicJdbcAdapter.findLog(log, pageable);
    }

    @ResponseBody
    @GetMapping("interfaceLogs")
    public Page<LogDto> findInterfaceLogList(
            @RequestParam(value = "name", required = false) String name,
            @PageableDefault Pageable pageable) {
        return dynamicJdbcAdapter.findGroupLog(name, pageable);
    }
}
