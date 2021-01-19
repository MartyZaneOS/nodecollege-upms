package com.nodecollege.cloud.feign;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.SysVisitLog;
import com.nodecollege.cloud.fallback.ArticleClientFallback;
import com.nodecollege.cloud.fallback.LogClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author LC
 * @date 2020/1/10 9:47
 */
@Primary
@FeignClient(value = "article", fallback = ArticleClientFallback.class)
public interface ArticleClient {

    /**
     * 定时处理前一天文章访问量
     */
    @PostMapping("/articleSync/countArticleVisitData")
    NCResult<Map<String, Integer>> countArticleVisitData(@RequestBody SysVisitLog visitLog);

    // 定时统计前一小时文章访问量
    @PostMapping("/articleSync/countArticleVisitHour")
    NCResult<Map<String, Integer>> countArticleVisitHour(@RequestBody SysVisitLog visitLog);
}
