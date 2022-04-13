package com.xx.rpc.common.bean;

/**
 * @Description: RPC Netty需要接收数据集对象
 * Author: XX
 * Date: 2022/4/10  13:45
 **/
public class RpcResponse {
    private String requestId;//请求ID
    private Exception exception;//反馈的异常对象
    private Object result;//调用远程方法返回的具体对象

    public boolean hasException() {
        return exception != null;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public Exception getException() {
        return exception;
    }
    public void setException(Exception exception) {
        this.exception = exception;
    }
    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
}
