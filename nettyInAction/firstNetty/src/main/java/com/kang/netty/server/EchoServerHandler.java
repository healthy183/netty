package com.kang.netty.server;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2016/8/20.
 * @Author Healthy
 * @Version
 */
@Slf4j
@ChannelHandler.Sharable //此ChannelHandler可以被多个Channel安全共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 每次收到消息时被调用
     * will be invoking when receive msg
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        log.info("server received : "+ in.toString(CharsetUtil.UTF_8));
        ctx.write(in);//将来消息写入发送方，并不刷新输出消息
    }

    /**
     * 用来通知handler上一个ChannelRead()是被这批消息中的最后一个消息调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //刷新挂起的数据至远程,然后关闭channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).
                addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * exception handler
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.info(Throwables.getStackTraceAsString(cause));
        ctx.close();//关闭channel
    }

}
