package com.nodecollege.cloud;

import com.nodecollege.cloud.core.netty.WSServerInitialzer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Value("${chat.inetPort:2080}")
    private Integer inetPort;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("##################################################");
        log.info("netty websocket server 开始启动");
        // Boss线程：由这个线程池提供的线程是boss种类的，用于创建、连接、绑定socket， （有点像门卫）然后把这些socket传给worker线程池。
        // 在服务器端每个监听的socket都有一个boss线程来处理。在客户端，只有一个boss线程来处理所有的socket。
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker线程：Worker线程执行所有的异步I/O，即处理操作
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workGroup);
            server.channel(NioServerSocketChannel.class);
            server.childHandler(new WSServerInitialzer());
            ChannelFuture future = server.bind(inetPort).sync();
            log.info("netty websocket server 启动完毕...");
            log.info("##################################################");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.info("netty websocket server 关闭...");
            log.info("##################################################");
        }
    }
}