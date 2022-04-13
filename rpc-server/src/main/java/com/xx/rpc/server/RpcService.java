package com.xx.rpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;


/**
 * @Description: RPC 服务注解（标注在服务实现类上）
 * Author: XX
 * Date: 2022/4/10  17:16
 **/
//@Target用于定义在注解的上边,表明该注解可以使用的范围。（这里为Type，即可用于/接口、类、枚举、注解）
@Target({ElementType.TYPE})
//@Retention可以用来修饰注解，其中RetentionPolicy指定注解的生命周期
//这里RUNTIME指注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在,因此可以在运行时通过反射读取
@Retention(RetentionPolicy.RUNTIME)
@Component//添加该注解，该类可以被Spring扫描到
public @interface RpcService {

    /**
     * 服务接口类
     */
    Class<?> value();

    /**
     * 服务版本号
     */
    String version() default "";
}
