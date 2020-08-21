package com.dm.data.writer.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wendongshan
 */
@Data
public class InterfaceLog  implements Serializable {

    private int id;
    private String name;
    private String tableName;
    private String databaseName;
    private LocalDateTime create_date;
    private String status;
    private String data;
    private String remark;
    private Integer num;

    /**
     * =================================================
     */
    /**
     * 开始时间
     */
    private LocalDateTime startDate;
    /**
     * 结束时间
     */
    private LocalDateTime endDate;
}
