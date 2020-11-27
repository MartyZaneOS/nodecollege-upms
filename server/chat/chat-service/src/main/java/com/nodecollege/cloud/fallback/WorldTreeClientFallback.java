package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.ChatDebate;
import com.nodecollege.cloud.feign.WorldTreeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author LC
 * @date 2019/11/10 21:14
 */
@Component
public class WorldTreeClientFallback implements WorldTreeClient {

    private static final Logger logger = LoggerFactory.getLogger(WorldTreeClientFallback.class);

    @Override
    public NCResult debateHandle(ChatDebate debate) {
        logger.error("debateHandle 服务间调用异常！");
        return NCResult.error(ErrorEnum.FEIGN_SERVICE_ERROR);
    }
}
