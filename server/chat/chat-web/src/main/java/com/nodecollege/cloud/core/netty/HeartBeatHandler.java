package com.nodecollege.cloud.core.netty;

import com.nodecollege.cloud.common.ChatConstants;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.core.SpringContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于检测channel的心跳handler
 * 继承ChannelInboundHandlerAdapter，从而不需要实现channelRead0方法
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // 判断evt是否是IdleStateEvent（用于触发用户事件，包含 读空闲/写空闲/读写空闲 ）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.debug("进入读空闲...");
            }
            if (event.state() == IdleState.WRITER_IDLE) {
                log.debug("进入写空闲...");
            }
            if (event.state() == IdleState.ALL_IDLE) {
                String key = ChatConstants.CHAT_HEART_BEAT + ctx.channel().id().asShortText();
                redisUtils.increment(key, 1L);
                Long time = redisUtils.get(key, Long.class);
                log.info("客户-{}，无连接 {} 分钟了", ctx.channel().id().asShortText(), time);
                log.info("users的数量为：" + ChatHandler.GLOBAL_USERS.size());
                if (time > 2 * 60) {
                    // 两个小时无连接信息，关闭无用的channel，以防资源浪费
                    ctx.channel().close();
                    log.info("channel关闭后，在线用户的数量为：" + ChatHandler.GLOBAL_USERS.size());
                }
            }
        }
    }
}
