package com.dm.data.writer.util;

import org.springframework.http.HttpStatus;

/**
 * 异常处理模型类
 *
 * @author wendongshan
 */
public class LogException extends RuntimeException {
    private String interfaceName;
    private Object data;
    private String msg;
    private HttpStatus status;

    public LogException() {

    }

    public LogException(String interfaceName, Object data, String msg, HttpStatus status) {
        this.interfaceName = interfaceName;
        this.data = data;
        this.msg = msg;
        this.status = status;
    }

    public LogException(String interfaceName, Object data, String msg) {
        this.interfaceName = interfaceName;
        this.data = data;
        this.msg = msg;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
