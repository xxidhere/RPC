package com.xx.rpc.sample.server;

import com.xx.rpc.api.HelloService;
import com.xx.rpc.api.Person;
import com.xx.rpc.server.RpcService;

/**
 * @Description: TODO
 * Author: XX
 * Date: 2022/4/11  14:56
 **/
@RpcService(HelloService.class) //RpcService用于暴露服务至注册中心
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String name) {
        return "Hello! "+name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! "+ person.getFirstName() + " " + person.getLastName();
    }

}
