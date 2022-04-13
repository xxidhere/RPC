package com.xx.rpc.registry;

/**
 * @Description: 服务注册接口
 * Author: XX
 * Date: 2022/4/11  9:39
 **/
public interface ServiceRegistry {

    /**
     * 注册服务名称与服务地址
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     * */
    void register(String serviceName,String serviceAddress);
}
