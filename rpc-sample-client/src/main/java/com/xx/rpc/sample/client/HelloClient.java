package com.xx.rpc.sample.client;

import com.xx.rpc.api.HelloService;
import com.xx.rpc.api.Person;
import com.xx.rpc.client.RpcProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Description: TODO
 * Author: XX
 * Date: 2022/4/11  15:30
 **/
public class HelloClient {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        System.out.println(result);

        HelloService helloService2 = rpcProxy.create(HelloService.class, "simple.hello2");
        String result2 = helloService2.hello("世界");
        System.out.println(result2);

        String result3 = helloService.hello(new Person("X", "X"));
        System.out.println(result3);

        System.exit(0);
    }
}
