package com.jd.platform.jlog.worker.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;


/**
 * 收到消息后的处理器
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-10
 */
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static int i = 0;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        //获得请求
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(Thread.currentThread().getName() + "--" + "接收到请求：" + req);
        //重新 new 一个DatagramPacket对象，我们通过packet.sender()来获取发送者的消息。重新发送出去！
        try {
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(
                            "answer" + i++,
                            CharsetUtil.UTF_8),
                    packet.sender())).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
        cause.printStackTrace();
    }

}
