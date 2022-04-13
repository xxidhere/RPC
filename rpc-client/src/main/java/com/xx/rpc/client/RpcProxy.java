package com.xx.rpc.client;

import com.xx.rpc.common.utils.StringUtil;
import com.xx.rpc.registry.ServiceDiscovery;
import com.xx.rpc.registry.zookeeper.ZooKeeperServiceDiscovery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xx.rpc.common.bean.RpcRequest;
import com.xx.rpc.common.bean.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @Description: 封装rpc信息，并调用RpcClient客户端对象，将rpc请求信息发送至服务地址对应的服务器中
 * RPC 代理，用于创建 RPC 服务代理
 * 使用Netty客户端发送rpc请求，并获取反馈信息，拿到相关的服务调用类的相关方法调用结果
 * Author: XX
 * Date: 2022/4/10  14:31
 **/

public class RpcProxy {
    //日志对象
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);
    //服务地址
    private String serviceAddress;
    //服务地址发现类，该类由注册中心实现
    private ServiceDiscovery serviceDiscovery;

    //当不需要注册中心时，直接传入服务地址即可
    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    //当需要注册中心时，传入注册中心的服务地址发现类对象
    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }


    //创建类方法
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    //创建类方法，带有服务版本参数
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        //创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), //类加载器
                new Class<?>[]{interfaceClass}, //代理类的类型
                new InvocationHandler() { //代理的处理类
                    //具体的代理方法
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建 RPC 请求对象并设置请求属性
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());//唯一的请求ID
                        request.setInterfaceName(method.getDeclaringClass().getName());//要调用的方法名
                        request.setServiceVersion(serviceVersion);//服务版本
                        request.setMethodName(method.getName());//要调用的方法名称
                        request.setParamterTypes(method.getParameterTypes());//设置调用方法的参数类型
                        request.setParameters(args);//设置调用方法的参数
                        //获取RPC服务地址
                        if (serviceDiscovery != null) {
                            //当serviceDiscovery对象不为空时，说明需要从注册中心获取服务地址
                            String serviceName = interfaceClass.getName();
                            if (StringUtil.isNotEmpty(serviceVersion)) {
                                serviceName += "-" + serviceVersion;//服务名称加版本号
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);//远程获取服务地址
                            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }
                        //如果服务地址为空，就报错
                        if (StringUtil.isEmpty(serviceAddress)) {
                            throw new RuntimeException("server address is empty");
                        }
                        // 从 RPC 服务地址中解析主机名与端口号
                        String[] array = StringUtils.split(serviceAddress, ":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        // 创建 RPC 客户端对象并发送 RPC 请求
                        RpcClient client = new RpcClient(host, port);
                        long time = System.currentTimeMillis();
                        RpcResponse response = client.send(request);//获取rpc请求的反馈对象
                        LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);//日志打印请求处理时间
                        if (response == null) {//如果反馈对象为空，则报错
                            throw new RuntimeException("response is null");
                        }
                        // 返回 RPC 响应结果
                        if (response.hasException()) {//是否有异常
                            throw response.getException();//反馈的异常对象
                        } else {
                            return response.getResult();//调用远程方法返回的具体对象
                        }
                    }
                });
    }

    //注册中心服务发现对象，还没有实现，这里先放一个空的
//    class ServiceDiscovery {
//
//        public String discover(String serviceName) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//    }
}
