package com.fanwe.lib.proxy;

/**
 * Created by zhengjun on 2017/11/30.
 */
public interface FProxy
{
    /**
     * 代理类的后缀名
     */
    String PROXY_CLASS_SUFFIX = "$FProxy$";
    /**
     * 代理类中可以调用父类方法的方法后缀名
     */
    String PROXY_CLASS_INVOKE_SUPER_METHOD_SUFFIX = "$Super$";
    /**
     * 代理类接口方法名
     */
    String METHOD_NAME_SETMETHODINTERCEPTOR = "setMethodInterceptor" + PROXY_CLASS_SUFFIX;

    void setMethodInterceptor$FProxy$(FMethodInterceptor interceptor);
}
