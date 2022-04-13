package com.xx.rpc.sample.client;

import com.xx.rpc.api.GoodByeService;
import com.xx.rpc.api.HelloService;
import com.xx.rpc.client.RpcProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Description: TODO
 * Author: XX
 * Date: 2022/4/13  9:45
 **/
public class GoodByeClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        GoodByeService goodbyeservice = rpcProxy.create(GoodByeService.class);
        String result = goodbyeservice.sayGoodBye("XX");
        System.out.println(result);

    }

}
