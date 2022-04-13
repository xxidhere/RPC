package com.xx.rpc.registry.zookeeper;


import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xx.rpc.registry.ServiceRegistry;

/**
 * @Description: 基于Zookeeper的服务注册接口实现
 * Author: XX
 * Date: 2022/4/11  9:46
 **/
public class ZooKeeperServiceRegistry implements ServiceRegistry{

    //日志对象
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private final ZkClient zkClient;//zookeeper连接客户端

    //构造方法，通过传入的zookeeper服务地址，创建zookeeper客户端
    public ZooKeeperServiceRegistry(String zkAddress){
        //创建Zookeeper客户端
        zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        //创建registry节点(持久)
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if(!zkClient.exists(registryPath)){
            //判断节点是否存在，节点不存在时，创建相关节点
            zkClient.createPersistent(registryPath);//创建持久化节点
            LOGGER.debug("create registry node:{}",registryPath);
        }
        //创建service节点(持久)
        String servicePath = registryPath + "/" + serviceName;
        if(!zkClient.exists(servicePath)){
            zkClient.createPersistent(servicePath);//创建持久化节点
            LOGGER.debug("create service node:{}",servicePath);
        }
        //创建address节点(临时)
        String addressPath  = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.debug("create address node:{}",addressNode);
    }

}
