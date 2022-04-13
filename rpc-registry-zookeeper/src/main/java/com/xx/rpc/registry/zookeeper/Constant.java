package com.xx.rpc.registry.zookeeper;

/**
 * @Description: 常量
 * Author: XX
 * Date: 2022/4/11  9:46
 **/

public interface Constant {

    int ZK_SESSION_TIMEOUT = 5000;//会话超时时间
    int ZK_CONNECTION_TIMEOUT = 1000;//连接超时时间

    String ZK_REGISTRY_PATH = "/registry";//注册地址
}
