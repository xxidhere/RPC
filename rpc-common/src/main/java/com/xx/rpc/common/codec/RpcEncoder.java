package com.xx.rpc.common.codec;


import com.xx.rpc.common.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description: rpc 编码器
 * Author: XX
 * Date: 2022/4/10  13:53
 **/
public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if(genericClass.isInstance(in)){//检测Object这个对象能不能被转化为这个类
            byte[] data = SerializationUtil.serialize(in);//将Object序列化为byte
            out.writeInt(data.length);//标记写入信息的长度
            out.writeBytes(data);//写入信息
        }
    }
}
