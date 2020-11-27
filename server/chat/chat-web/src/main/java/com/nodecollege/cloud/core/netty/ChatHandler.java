package com.nodecollege.cloud.core.netty;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.exception.BaseException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.core.SpringContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LC
 * @date 2020/2/27 14:42
 */
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private ChatDispatchServlet chatDispatchServlet = SpringContextHolder.getBean(ChatDispatchServlet.class);

    /**
     * 用于记录和管理所有客户端的channle
     */
    public static ChannelGroup GLOBAL_USERS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端传输过来的消息
        try {
            chatDispatchServlet.handler(msg.text(), ctx.channel());
        } catch (BaseException e) {
            log.error("业务处理失败！", e);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(NCResult.error(e.getCode(), e.getMessage()))));
        } catch (Exception e) {
            log.error("消息处理失败！", e);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(NCResult.error("-1", "聊天室消息处理失败！"))));
        }
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("客户建立连接 channelId:{}", ctx.channel().id().asShortText());
        GLOBAL_USERS.add(ctx.channel());
    }

    /**
     * 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端被移除，channelId：{}", ctx.channel().id().asShortText());
        GLOBAL_USERS.remove(ctx.channel());
    }

    /**
     * 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常 关闭连接, channelId：{}", ctx.channel().id().asShortText(), cause);
        ctx.channel().close();
        GLOBAL_USERS.remove(ctx.channel());
    }
}
