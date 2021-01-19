package com.nodecollege.cloud.common.model.po;

import java.util.Date;
import lombok.Data;

/**
 * Table: o_data_power_auth
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-12-14 18:06:32
 */
@Data
public class OperateDataPowerAuth {
    /**
     * id
     */
    private Long id;

    /**
     * 数据权限代码
     */
    private String dataPowerCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 所有数据
     */
    private Boolean allData;

    /**
     * 授权权限列表 null-无权访问，空-访问所有
     */
    private String authList;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态 0-不允许修改
     */
    private Integer state;
}