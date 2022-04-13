package com.xx.rpc.client;

import com.xx.rpc.common.bean.RpcRequest;
import com.xx.rpc.common.bean.RpcResponse;
import com.xx.rpc.common.codec.RpcDecoder;
import com.xx.rpc.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 利用Netty进行tcp请求发送的类
 *               RPC 客户端，用于发送rpc请求
 *               继承SimpleChannelInboundHandler，将自己作为一个InboundHandler
 * Author: XX
 * Date: 2022/4/10  14:31
 **/
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{
    //日志对象
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
    //传输信息的服务端的ip和端口
    private final String host;
    private final int port;
    //服务端反馈的response信息对象
    private RpcResponse response;
    //构造方法，用于传输服务端的信息
    public RpcClient(String host,int port){
        this.host = host;
        this.port = port;
    }

    //处理管道读取反馈的response对象，这里只需获取response对象即可。
    @Override
    protected void channelRead0(ChannelHandlerContext arg0, RpcResponse response) throws Exception {
        this.response = response;
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception",cause);//日志记录异常原因
        ctx.close();//关闭上下文对象
    }

    //使用Netty发送rpc请求
    public RpcResponse send(RpcRequest request) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //创建并初始化Netty客户端Bootstrap对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>(){

                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class));//注册编码器
                    pipeline.addLast(new RpcDecoder(RpcResponse.class));//注册解码器
                    pipeline.addLast(RpcClient.this);//注册客户端处理对象
                }
            });
            //设置无延迟操作
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            //连接RPC服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //写入RPC请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            return response;
        }finally{
            group.shutdownGracefully();
        }
    }
}
