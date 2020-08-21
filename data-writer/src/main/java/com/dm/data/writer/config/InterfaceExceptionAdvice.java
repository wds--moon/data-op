package com.dm.data.writer.config;


import com.alibaba.fastjson.JSON;
import com.dm.data.writer.component.DynamicJdbcComponent;
import com.dm.data.writer.component.JdbcComponent;
import com.dm.data.writer.entity.TableInfo;
import com.dm.data.writer.util.ConstantUitl;
import com.dm.data.writer.util.LogException;
import com.dm.data.writer.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author wendongshan
 */
@RestControllerAdvice
public class InterfaceExceptionAdvice {

    @Autowired
    private DynamicJdbcComponent dynamicJdbcComponent;

    @Autowired
    private JdbcComponent jdbcComponent;

    @ResponseBody
    @ExceptionHandler(LogException.class)
    public ResponseEntity<?> interfaceLogExceptionHandler(LogException e) {
        /**
         * 先保存日志
         */
        TableInfo tableInfo = dynamicJdbcComponent.findByInterfaceName(e.getInterfaceName());
        jdbcComponent.setSaveLog(e.getInterfaceName(), JSON.toJSONString(e.getData()), ConstantUitl.Operation.FAIL, e.getMsg(),0,tableInfo.getDatabase(),tableInfo.getTableName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, e.getInterfaceName(), e.getMsg())) ;
    }

    /**
     * 对于运行时候的异常全部指定为系统异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> exceptionHandler(RuntimeException e) {
        /**
         * 先保存日志
         */
        jdbcComponent.setSaveLog("system error", null, ConstantUitl.Operation.FAIL, e.getLocalizedMessage(),0,null,null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e.getLocalizedMessage())) ;
    }
}
