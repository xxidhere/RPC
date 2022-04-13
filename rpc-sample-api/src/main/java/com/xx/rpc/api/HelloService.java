package com.xx.rpc.api;

/**
 * @Description: TODO
 * Author: XX
 * Date: 2022/4/11  14:46
 **/
public interface HelloService {

    String hello(String name);

    String hello(Person person);
}
