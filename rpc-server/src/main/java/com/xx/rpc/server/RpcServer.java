package com.xx.rpc.server;

import java.util.HashMap;
import java.util.Map;

import com.xx.rpc.registry.ServiceRegistry;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;

import com.xx.rpc.common.bean.RpcRequest;
import com.xx.rpc.common.bean.RpcResponse;
import com.xx.rpc.common.codec.RpcDecoder;
import com.xx.rpc.common.codec.RpcEncoder;
import com.xx.rpc.common.utils.StringUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;




/**
 * @Description: RPC 服务器，用于发布 RPC 服务
 *
 * Author: XX
 * Date: 2022/4/10  17:14
 **/
//实现ApplicationContextAware是为了获取该类所在的Spring容器，以操作spring容器及其中的Bean实例。
//    ApplicationContextAware接口是用来获取框架自动初始化的ioc容器对象的。
//实现InitializingBean(提供了初始化方法的方式)是为了在初始化Bean之前加一些逻辑(afterPropertiesSet方法中)
public class RpcServer implements ApplicationContextAware, InitializingBean {
    //日志对象
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    //服务地址
    private String serviceAddress;
    //服务地址注册类，该类由注册中心实现
    private ServiceRegistry serviceRegistry;

    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * 扫描带有RpcService注解的类，并初始化HandlerMap对象
     * @param ctx
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        //扫描带有RpcService注解的类，并初始化HandlerMap对象
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            //遍历带有RpcService注解的类
            for (Object serviceBean : serviceBeanMap.values()) {
                //获取注解对象
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                //通过注解对象，获取其修饰参数(serviceName服务名称，serviceVersion服务版本)
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                if (StringUtil.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;//服务名与版本号拼接
                }
                handlerMap.put(serviceName, serviceBean);//存放进映射Map，供Handler处理类操作
            }
        }
    }

    /** 动态代理的 service 之前，先通过 Netty 将服务注册到 zookeeper 注册中心中
     * 在初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 服务端 Bootstrap 对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class)); // 解码 RPC 请求
                    pipeline.addLast(new RpcEncoder(RpcResponse.class)); // 编码 RPC 响应
                    pipeline.addLast(new RpcServerHandler(handlerMap)); // 处理 RPC 请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 获取 RPC 服务器的 IP 地址与端口号
            String[] addressArray = StringUtil.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            // 启动 RPC 服务器
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            // 注册 RPC 服务地址
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    LOGGER.debug("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            LOGGER.debug("server started on port {}", port);
            // 关闭 RPC 服务器
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    //注册中心服务注册对象，还没有实现，这里先放一个空的
//    class ServiceRegistry {
//
//        public String register(String interfaceName, String serviceAddress) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//    }
}
