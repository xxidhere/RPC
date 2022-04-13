package com.xx.rpc.common.bean;

/**
 * @Description: RPC Netty需要传输数据集对象
 * Author: XX
 * Date: 2022/4/10  13:45
 **/
public class RpcRequest {
    private String requestId;//请求ID
    private String interfaceName;//接口名称
    private String serviceVersion;//服务版本
    private String methodName;//需要调用的方法名
    private Class<?>[] paramterTypes;//方法参数类型
    private Object[] parameters;//方法参数
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getInterfaceName() {
        return interfaceName;
    }
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    public String getServiceVersion() {
        return serviceVersion;
    }
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public Class<?>[] getParamterTypes() {
        return paramterTypes;
    }
    public void setParamterTypes(Class<?>[] paramterTypes) {
        this.paramterTypes = paramterTypes;
    }
    public Object[] getParameters() {
        return parameters;
    }
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
