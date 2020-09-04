package com.dm.data.writer.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
public class TestJdbc {

    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;

    @Autowired
    private ObjectMapper om;

    @Test
    public void testWrite() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id_", "123");
        data.put("name_", "teacher");
        String sql = generaterInsertSql("test_", data);
        jdbcOperations.update(sql, data);

        // TODO 下面这个时批处理模板
//        jdbcOperations.batchUpdate(sql, (Map<String, ?>[]) Collections.singleton(data).toArray());

    }

    @Test
    public void testWriteJson() throws JsonMappingException, JsonProcessingException {
        String json = "{\"id\": 3,\"name\": \"大神\"}";
        Map<String, Object> data = parasJson(json);
        String sql = generateUpdateSql("test_", data);
        System.out.println(sql);
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> parasJson(String str) throws JsonMappingException, JsonProcessingException {
        return om.readValue(str, Map.class);
    }

    private String generaterInsertSql(String table, Map<String, Object> values) {
        StringBuilder builder = new StringBuilder();
        Set<String> keys = values.keySet();
        keys = keys.stream().map(key -> ":" + key).collect(Collectors.toSet());
        builder.append("insert into ").append(table).append("(").append(StringUtils.join(keys, ",").replace(":", "")).append(")").append(" values (")
                .append(StringUtils.join(keys, ","))
                .append(")");
        return builder.toString();
    }

    private String generateUpdateSql(String table, Map<String, Object> values) {
        StringBuilder builder = new StringBuilder();
        Set<String> keys = values.keySet().stream().filter(key -> !key.equals("id")).map(key -> key + "= :" + key).collect(Collectors.toSet());
        Set<String> ids = values.keySet().stream().filter(key -> key.equals("id")).map(key -> key + "= :" + key).collect(Collectors.toSet());
        /**
         * update table set a=:a,b=:b
         */
        builder.append("update ").append(table).append(" set ").append(StringUtils.join(keys, ",")).append(" where ").append(StringUtils.join(ids, ","));
        return builder.toString();
    }
}
