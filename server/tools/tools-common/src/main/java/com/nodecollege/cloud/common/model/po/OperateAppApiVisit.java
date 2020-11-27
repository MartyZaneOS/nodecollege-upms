package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Objects;

/**
 * Table: upms_interface_visit
 * 版权：节点学院
 *
 * @author LC
 * @date 2019-12-20 18:22:52
 */
@Data
public class OperateAppApiVisit extends OperateAppApi {
    /**
     * 接口访问id
     */
    private Long apiVisitId;

    /**
     * 接口id
     */
    private Long apiId;

    /**
     * 访问日期 yyyymmdd
     */
    private String visitDate;

    /**
     * 访问小时 HH
     */
    private String visitH;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 访客ip加端口
     */
    private String requestIp;

    /**
     * 访问来源
     */
    private String accessSource;

    /**
     * 访问量
     */
    private Long visitCount;
    /**
     * 本地ip加端口
     */
    private String localIpPort;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OperateAppApiVisit that = (OperateAppApiVisit) o;
        return Objects.equals(apiId, that.apiId) &&
                Objects.equals(visitDate, that.visitDate) &&
                Objects.equals(visitH, that.visitH) &&
                Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(requestIp, that.requestIp) &&
                Objects.equals(accessSource, that.accessSource) &&
                Objects.equals(localIpPort, that.localIpPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), apiId, visitDate, visitH, tenantId, requestIp, accessSource, localIpPort);
    }
}