package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.DebateDTO;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatDebate;
import com.nodecollege.cloud.common.model.po.ChatDebateRecord;
import com.nodecollege.cloud.common.model.po.ChatDebateRelation;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.DebateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 辩论堂
 *
 * @author LC
 * @date 2020/4/19 13:46
 */
@RestController
@RequestMapping("/debate")
public class DebateController {

    @Autowired
    private DebateService debateService;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "辩论堂", description = "添加辩论堂！")
    @PostMapping("/addDebate")
    public NCResult addDebate(@RequestBody ChatDebate debate) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        debate.setUserId(loginUser.getLoginId());
        debateService.addDebate(debate);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "辩论堂", description = "查询辩论堂列表！")
    @PostMapping("/getDebateList")
    public NCResult<ChatDebate> getDebateList(@RequestBody QueryVO<ChatDebate> queryVO) {
        return debateService.getDebateList(queryVO);
    }

    @ApiAnnotation(modularName = "辩论堂", description = "查询辩论堂缓存！")
    @PostMapping("/getDebateCache")
    public NCResult<ChatDebate> getDebateCache(@RequestBody ChatDebate debate) {
        return NCResult.ok(debateService.getDebateCache(debate.getDebateId()));
    }

    @ApiAnnotation(modularName = "辩论堂", description = "添加辩论堂-内部接口！", accessSource = 2)
    @PostMapping("/addDebateApi")
    public NCResult addDebateApi(@RequestBody DebateDTO debate) {
        debateService.addDebateApi(debate);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "辩论堂", description = "根据节点信息查询关联辩论堂信息！")
    @PostMapping("/getRelation")
    public NCResult<ChatDebateRelation> getRelation(@RequestBody QueryVO<ChatDebateRelation> queryVO) {
        return debateService.getRelation(queryVO);
    }

    @ApiAnnotation(modularName = "辩论堂", description = "查询辩论记录列表！")
    @PostMapping("/getRecordList")
    public NCResult<ChatDebateRecord> getRecordList(@RequestBody QueryVO<ChatDebateRecord> queryVO) {
        return debateService.getRecordList(queryVO);
    }
}
