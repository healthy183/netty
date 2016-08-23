package com.kang.netty.server;

import com.google.common.base.Throwables;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
public class EchoServer {
    private final int port;
    public  EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 705;
        new EchoServer(port).start();
    }

    public void start(){
        final EchoServerHandler serverandler = new EchoServerHandler();
        EventLoopGroup eventLoopGroup =  new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap =   new ServerBootstrap();
            serverBootstrap
                    .group(eventLoopGroup)//指定组
                    .channel(NioServerSocketChannel.class)//指定channel类型
                    .localAddress(new InetSocketAddress(port))//指定port
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //channel的ChannelPipeline添加EchoServerHandler
                            ch.pipeline().addLast(serverandler);//EchoServerHandler是Sharable,所以可以共用此实例
                        }
                    });
            ChannelFuture channelFuture =  serverBootstrap.bind().sync();//异地绑定服务器,sync()一直等到绑定完成
            channelFuture.channel().closeFuture().sync();//获取此channel的closeFuture,阻塞当前线程直至关闭完成
        }catch (InterruptedException ex){
            log.info(Throwables.getStackTraceAsString(ex));
        }catch (Exception ex){
            log.info(Throwables.getStackTraceAsString(ex));
        }finally {
            try {
                eventLoopGroup.shutdownGracefully().sync();//关闭eventLoopGroup,释放资源
            } catch (InterruptedException e) {
                log.info(Throwables.getStackTraceAsString(e));
            }
        }
    }

}
