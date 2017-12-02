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
     * 属性：拦截对象
     */
    String FIELD_NAME_METHODINTERCEPTOR = "mMethodInterceptor";
    /**
     * 方法：设置拦截对象
     */
    String METHOD_NAME_SETMETHODINTERCEPTOR = "setMethodInterceptor" + PROXY_CLASS_SUFFIX;
    /**
     * 方法：返回拦截对象
     */
    String METHOD_NAME_GETMETHODINTERCEPTOR = "getMethodInterceptor" + PROXY_CLASS_SUFFIX;

    /**
     * 设置拦截对象
     *
     * @param interceptor
     */
    void setMethodInterceptor$FProxy$(FMethodInterceptor interceptor);

    /**
     * 返回拦截对象
     *
     * @return
     */
    FMethodInterceptor getMethodInterceptor$FProxy$();
}
