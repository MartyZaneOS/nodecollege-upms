package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.model.NCResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LC
 * @date 2020/6/15 16:39
 */
@RestController
public class WebsocketController {

    @Value("${chat.url:localhost}")
    private String wsUrl;

    @Value("${chat.inetPort:2080}")
    private Integer inetPort;

    @ApiAnnotation(modularName = "聊天室", description = "获取websocket地址")
    @PostMapping("/getWebsocketUrl")
    public NCResult<Map<String, String>> getWebsocketUrl(){
        Map<String, String> map = new HashMap<>();
        map.put("chatWs", "ws://" + wsUrl + ":" + inetPort + "/ws");
        return NCResult.ok(map);
    }
}
