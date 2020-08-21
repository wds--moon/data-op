package com.dm.data.writer.util;

import java.io.Serializable;

/**
 * 指定返回类型
 * @author wendongshan
 */
public class Result implements Serializable {

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 请求数据
     */
    private Object data;

    /**
     * 访问接口
     */
    private String interfaceName;

    /**
     * 提示信息
     */
    private String message;

    public Result() {
    }

    public Result(Integer status, Object data, String interfaceName, String message) {
        this.status = status;
        this.data = data;
        this.interfaceName = interfaceName;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
