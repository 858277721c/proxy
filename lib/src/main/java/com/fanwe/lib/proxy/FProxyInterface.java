package com.fanwe.lib.proxy;

/**
 * 代理要实现的接口
 */
public interface FProxyInterface
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

    String FIELD_NAME_METHODINTERCEPTOR = "mMethodInterceptor";


    /**
     * 设置方法拦截对象
     *
     * @param interceptor
     */
    void setMethodInterceptor$FProxy$(FMethodInterceptor interceptor);
}
