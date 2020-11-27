package com.nodecollege.cloud.common.model;

import com.nodecollege.cloud.common.model.vo.ChatActiveData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 未读事件
 *
 * @author LC
 * @date 2020/3/17 20:35
 */
@Getter
public class UnreadEvent extends ApplicationEvent {

    private List<ChatActiveData> activeDataList;

    public UnreadEvent(Object source, List<ChatActiveData> activeDataList) {
        super(source);
        this.activeDataList = activeDataList;
    }
}
