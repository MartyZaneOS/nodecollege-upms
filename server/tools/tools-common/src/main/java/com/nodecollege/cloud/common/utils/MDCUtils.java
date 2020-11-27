package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.constants.NCConstants;
import org.slf4j.MDC;

import java.util.Map;

/**
 * @author LC
 * @date 2019/11/30 17:27
 */
public class MDCUtils {

    public static void handle(Map mdcAdapter){
        MDC.put(NCConstants.REQUEST_ID, mdcAdapter.get(NCConstants.REQUEST_ID).toString());
        if (NCUtils.isNullOrEmpty(mdcAdapter.get(NCConstants.THREAD_ID))){
            // 线程id为空，新建id
            MDC.put(NCConstants.THREAD_ID, NCUtils.getRequestId());
        }else if (NCUtils.isNullOrEmpty(mdcAdapter.get(NCConstants.THREAD_ID2))){
            // 线程id不为空， 线程id2为空，线程id2新建id
            MDC.put(NCConstants.THREAD_ID, mdcAdapter.get(NCConstants.THREAD_ID).toString());
            MDC.put(NCConstants.THREAD_ID2, NCUtils.getRequestId());
        }else {
            // 线程id和线程id2都不为空，线程id 赋 线程id2的值， 线程id2 新建id
            MDC.put(NCConstants.THREAD_ID, mdcAdapter.get(NCConstants.THREAD_ID2).toString());
            MDC.put(NCConstants.THREAD_ID2, NCUtils.getRequestId());
        }
    }
}
