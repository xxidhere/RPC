package com.xx.rpc.sample.server;

import com.xx.rpc.api.GoodByeService;
import com.xx.rpc.server.RpcService;

/**
 * @Description: TODO
 * Author: XX
 * Date: 2022/4/13  9:42
 **/
@RpcService(GoodByeService.class)
public class GoodByeServiceImpl implements GoodByeService {
    @Override
    public String sayGoodBye(String s) {
        return "good bye : "+s;
    }
}
