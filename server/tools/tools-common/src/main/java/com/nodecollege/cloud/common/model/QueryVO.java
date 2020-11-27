package com.nodecollege.cloud.common.model;

import com.nodecollege.cloud.common.utils.BeanUtils;
import lombok.Data;

import java.util.Map;

/**
 * @author LC
 * @date 2019/11/9 17:37
 */
@Data
public class QueryVO<T> extends AbstractQueryVO {
    /**
     * 业务数据
     */
    private T data;

    public QueryVO() {
    }

    public QueryVO(T t) {
        this.data = t;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> obj = BeanUtils.javabean2Map(this);
        if (this.data != null) {
            Map<String, Object> dataObj = BeanUtils.javabean2Map(this.data);
            for (Map.Entry<String, Object> entry : dataObj.entrySet()) {
                if (entry.getValue() != null) {
                    obj.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return obj;
    }
}
