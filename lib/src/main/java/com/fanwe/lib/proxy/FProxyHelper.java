package com.fanwe.lib.proxy;

/**
 * 这个类的方法为代理对象调用的
 */
public class FProxyHelper
{
    public static final String METHOD_NAME_NOTIFYINTERCEPTOR = "notifyInterceptor";

    /**
     * 通知方法拦截对象
     *
     * @param interceptor
     * @param proxyClass  代理的class
     * @param methodName  方法名称
     * @param argsClass   参数的class组
     * @param argsValue   参数值数组
     * @param proxy       代理对象
     * @return 返回拦截对象的返回值
     */
    public static Object notifyInterceptor(FMethodInterceptor interceptor,
                                           Class proxyClass, String methodName, Class[] argsClass, Object[] argsValue,
                                           Object proxy)
    {
        FMethodProxy methodProxy = new FMethodProxy(proxy, methodName, argsClass);
        return interceptor.intercept(proxy, argsValue, methodProxy);
    }
}
