package com.nodecollege.cloud.common.model;

import com.nodecollege.cloud.common.utils.BeanUtils;
import lombok.Data;

import java.util.Map;

/**
 * 查询关系数据实体
 *
 * @author LC
 * @date 2020/1/5 21:18
 */
@Data
public class QueryRelationVO<S, E> extends AbstractQueryVO {
    /**
     * 关系开始节点
     */
    private S startNode;
    /**
     * 关系结束节点
     */
    private E endNode;

    public QueryRelationVO() {
    }

    public QueryRelationVO(S s, E e) {
        this.startNode = s;
        this.endNode = e;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> obj = BeanUtils.javabean2Map(this);
        if (this.startNode != null) {
            Map<String, Object> dataObj = BeanUtils.javabean2Map(this.startNode);
            for (Map.Entry<String, Object> m : dataObj.entrySet()) {
                if (m.getValue() != null) {
                    obj.put(m.getKey(), m.getValue());
                }
            }
        }
        if (this.endNode != null) {
            Map<String, Object> dataObj = BeanUtils.javabean2Map(this.endNode);
            for (Map.Entry<String, Object> m : dataObj.entrySet()) {
                if (m.getValue() != null) {
                    obj.put(m.getKey(), m.getValue());
                }
            }
        }
        return obj;
    }
}
