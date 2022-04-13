package com.xx.rpc.registry;

/**
 * @Description: 服务发现接口
 * Author: XX
 * Date: 2022/4/11  9:39
 **/
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     * @return 服务地址
     * */
    String discover(String serviceName);
}
