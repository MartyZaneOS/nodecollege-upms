package com.nodecollege.cloud.common.model.vo;

import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import lombok.Data;

import java.util.List;

/**
 * @author LC
 * @date 2020/2/24 21:05
 */
@Data
public class AddGroupVO extends ChatGroupUser {

    /**
     * 添加用户主键列表
     */
    private List<Long> userList;
}
