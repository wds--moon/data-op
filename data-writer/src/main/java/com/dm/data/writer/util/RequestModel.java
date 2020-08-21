package com.dm.data.writer.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wendongshan
 */
@Data
public class RequestModel implements Serializable {
    /**
     * 成功入库 id集合
     */
    private List ids;
    /**
     * 成功条数
     */
    private Integer num;

}
