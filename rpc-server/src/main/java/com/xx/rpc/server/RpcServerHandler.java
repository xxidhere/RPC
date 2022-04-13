package com.xx.rpc.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xx.rpc.common.bean.RpcRequest;
import com.xx.rpc.common.bean.RpcResponse;
import com.xx.rpc.common.utils.StringUtil;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

/**
 * @Description: RPC服务端处理器，用于处理RPC请求
 * Author: XX
 * Date: 2022/4/10  17:14
 **/
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest>{
    //日志对象
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    //服务名以及相关服务类的Map对象
    private final Map<String,Object> handlerMap;
    //构造方法获取相关Map对象
    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 处理rpc客户端发来的服务调用 响应
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        //创建并初始化RPC响应对象
        RpcResponse response = new RpcResponse();
        //反馈发来的独一无二的请求ID
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Exception e) {
            //如果报错，首先记录日志，然后在返回对象中将报错信息放入
            LOGGER.error("handle result failure",e);
            response.setException(e);
        }
        //写入RPC响应对象并自动关闭连接
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * //拿到RPC客户端请求的服务信息，将相关服务(服务名、版本)加载出来，反馈给客户端
     * @param request
     * @return
     * @throws Exception
     */
    private Object handle(RpcRequest request) throws Exception {
        //获取服务对象
        String serviceName = request.getInterfaceName();//获取请求的服务接口名
        String serviceVersion = request.getServiceVersion();//获取请求的服务版本
        if (StringUtil.isNotEmpty(serviceVersion)) {//如果版本不为空，则拼接上去
            serviceName += "-" + serviceVersion;
        }
        //从加载出来的所有服务类中找到客户端请求的serviceName对应的具体Bean
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {//如果请求的相关类不存在，则进行报错
            throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
        }
        //如果请求的相关类存在，则通过反射获取调用该类相关请求方法所需的参数
        Class<?> serviceClass = serviceBean.getClass();//获取目标类的类型
        String methodName = request.getMethodName();//获取请求调用的方法名
        Class<?>[] paramterTypes = request.getParamterTypes();//获取请求调用的参数类型
        Object[] paramters = request.getParameters();//获取请求调用的具体参数类
        //使用CGLib执行反射调用(可以简单地通过反射执行方法（）)
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, paramterTypes);
        return serviceFastMethod.invoke(serviceBean, paramters);//返回调用具体方法的结果
    }

    /**
     * //读取响应时发生异常的处理方法
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
