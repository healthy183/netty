package com.kang.netty.client;

import com.google.common.base.Throwables;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2016/8/20.
 * @Author Healthy
 * @Version
 */
@Slf4j
public class EchoClient {

    private final String host;
    private final int port;


    public EchoClient(String host,int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        new EchoClient("localhost",705).start();
    }

    public void start(){
        EventLoopGroup eventLoopGroup =  new NioEventLoopGroup();
        try {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host,port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                });
            ChannelFuture channelFuture = bootstrap.connect().sync();//连接远程服务直至连接完成
            channelFuture.channel().closeFuture().sync();//阻塞到channel关闭
        } catch (InterruptedException e) {
            log.info(Throwables.getStackTraceAsString(e));
        }finally {
            try {
                eventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.info(Throwables.getStackTraceAsString(e));
            }
        }
    }
}
