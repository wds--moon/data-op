package com.dm.data.writer.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 接口获取参数格式
 *
 * @author wendongshan
 */

@Data
public class DataModel {
    /**
     * 接口名称
     */
    @ApiModelProperty("接口名称")
    private String interfaceName;
    /**
     * 数据格式
     */
    @ApiModelProperty("数据")
    private List<LinkedHashMap<String, Object>> data;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataModel dataModel = (DataModel) o;

        return new EqualsBuilder()
                .append(interfaceName, dataModel.interfaceName)
                .append(data, dataModel.data)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(interfaceName)
                .append(data)
                .toHashCode();
    }
}
