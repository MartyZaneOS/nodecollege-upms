package com.nodecollege.cloud.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.*;

/**
 * @author LC
 * @date 2020/9/9 20:17
 */
@Data
public class NCTreeVO<T> implements Comparable<NCTreeVO> {
    /**
     * 主键
     */
    private Long id;
    /**
     * 树名称
     */
    private String title;
    /**
     * 树值
     */
    private String key;
    /**
     * 值
     */
    private String value;
    /**
     * 父级树值
     */
    private String parentKey;
    /**
     * 排序号
     */
    private Integer num;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;
    /**
     * 原始数据
     */
    private T sourceData;
    /**
     * 子集
     */
    private List<NCTreeVO<T>> children;

    public NCTreeVO () {}

    public NCTreeVO (T t) {
        this.sourceData = t;
    }

    public NCTreeVO (String title, String key, String parentKey, Integer num) {
        this.title = title;
        this.key = key;
        this.value = key;
        this.parentKey = parentKey;
        this.num = num;
    }

    public NCTreeVO (Long id, String title, String key, String parentKey, Integer num, Date createTime) {
        this.id = id;
        this.title = title;
        this.key = key;
        this.value = key;
        this.parentKey = parentKey;
        this.num = num;
        this.createTime = createTime;
    }

    public NCTreeVO (T t, String title, String key, String parentKey, Integer num) {
        this.sourceData = t;
        this.title = title;
        this.key = key;
        this.value = key;
        this.parentKey = parentKey;
        this.num = num;
    }

    public NCTreeVO (T t, Long id, String title, String key, String parentKey, Integer num, Date createTime) {
        this.id = id;
        this.sourceData = t;
        this.title = title;
        this.key = key;
        this.value = key;
        this.parentKey = parentKey;
        this.num = num;
        this.createTime = createTime;
    }

    public static <T> List<NCTreeVO<T>> getTree(List<NCTreeVO<T>> sourceList) {
        Set<String> allCode = new HashSet<>();
        sourceList.forEach(item -> allCode.add(item.getKey()));

        Set<String> topParent = new HashSet<>();
        for (int i = 0; i < sourceList.size(); i++) {
            String parentCode = sourceList.get(i).getParentKey();
            if (!topParent.contains(parentCode) && !allCode.contains(parentCode)) {
                topParent.add(parentCode);
            }
        }
        List<NCTreeVO<T>> orgTreeList = new ArrayList<>();
        for (String parentCode : topParent) {
            orgTreeList.addAll(getTree(sourceList, parentCode));
        }
        return orgTreeList;
    }

    /**
     * 获取Tree
     */
    public static <T> List<NCTreeVO<T>> getTree(List<NCTreeVO<T>> orgList, String parentCode) {
        List<NCTreeVO<T>> orgTreeList = new ArrayList<>();
        Iterator<NCTreeVO<T>> orgIterator = orgList.iterator();
        while (orgIterator.hasNext()) {
            NCTreeVO org = orgIterator.next();
            if (org.getParentKey().equals(parentCode)) {
                orgTreeList.add(org);
                orgIterator.remove();
            }
        }
        if (orgTreeList.isEmpty()) {
            return null;
        }
        Collections.sort(orgTreeList);
        for (NCTreeVO orgTreeVO : orgTreeList) {
            orgTreeVO.setChildren(getTree(orgList, orgTreeVO.getKey()));
        }
        return orgTreeList;
    }

    @Override
    public int compareTo(NCTreeVO o) {
        return this.getNum().compareTo(o.getNum());
    }
}
