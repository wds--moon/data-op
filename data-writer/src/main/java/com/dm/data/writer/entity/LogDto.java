package com.dm.data.writer.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class LogDto  implements Serializable {

    private int id;
    private String name;
    private String tableName;
    private String databaseName;
    private LocalDateTime createDate;
    private String sum;
    private Integer num;
}
