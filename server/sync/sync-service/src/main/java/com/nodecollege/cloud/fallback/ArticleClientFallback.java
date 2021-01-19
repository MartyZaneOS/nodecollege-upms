package com.nodecollege.cloud.fallback;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.SysVisitLog;
import com.nodecollege.cloud.feign.ArticleClient;
import com.nodecollege.cloud.feign.LogClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author LC
 * @date 2020/1/10 9:50
 */
@Slf4j
@Component
public class ArticleClientFallback implements ArticleClient {

    @Override
    public NCResult<Map<String, Integer>> countArticleVisitData(@RequestBody SysVisitLog visitLog) {
        log.error("article-countArticleVisitData 统计文章前一天访问数据！中台熔断器——调用接口出现异常！");
        throw new NCException(ErrorEnum.FEIGN_SERVICE_ERROR);
    }

    @Override
    public NCResult<Map<String, Integer>> countArticleVisitHour(SysVisitLog visitLog) {
        log.error("article-countArticleVisitHour 定时统计前一小时文章访问量！中台熔断器——调用接口出现异常！");
        throw new NCException(ErrorEnum.FEIGN_SERVICE_ERROR);
    }
}
