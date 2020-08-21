package com.dm.data.writer.config;

import com.alibaba.fastjson.JSON;
import com.dm.data.writer.component.DynamicJdbcComponent;
import com.dm.data.writer.component.JdbcComponent;
import com.dm.data.writer.entity.TableInfo;
import com.dm.data.writer.util.ConstantUitl;
import com.dm.data.writer.util.DataModel;
import com.dm.data.writer.util.FieldsNameUtil;
import com.dm.data.writer.util.LogException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 处理操作日志
 *
 * @author wendongshan
 */
@Aspect
@Component
public class LogAspect {


    @Autowired
    private JdbcComponent jdbcComponent;

    @Autowired
    private DynamicJdbcComponent dynamicJdbcComponent;

    @Pointcut("execution(public * com.dm.data.writer.component.JdbcComponent.save(..))")
    public void addAdvice() {
    }

    @Around("addAdvice()")
    public Object interceptor(ProceedingJoinPoint pjp) throws LogException {
        Object object = null;
        DataModel model = new DataModel();
        try {
            Map<String, Object> fieldsName = FieldsNameUtil.getFieldsName(pjp);
            model = (DataModel) fieldsName.get("model");
            object = pjp.proceed();
        } catch (Throwable e) {
            throw new LogException(model.getInterfaceName(), model.getData(), e.getLocalizedMessage());
        }
        TableInfo tableInfo = dynamicJdbcComponent.findByInterfaceName(model.getInterfaceName());
        jdbcComponent.setSaveLog(model.getInterfaceName(), JSON.toJSONString(model.getData()), ConstantUitl.Operation.SUCCESS, "", model.getData() == null ? 0 : model.getData().size(),tableInfo.getDatabase(),tableInfo.getTableName());
        return object;
    }

    @After("addAdvice()")
    public void doAfter(JoinPoint pjp) {

    }


}
