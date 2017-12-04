package com.fanwe.lib.proxy;

import java.lang.reflect.Method;

/**
 * 这个类的方法为代理对象调用的
 */
public class FProxyHelper
{
    public static final String METHOD_NAME_NOTIFYINTERCEPTOR = "notifyInterceptor";

    /**
     * 通知方法拦截对象
     *
     * @param methodName 方法名称
     * @param argsClass  参数class
     * @param argsValue  参数值
     * @param proxy      代理对象
     * @return 返回拦截对象的返回值
     */
    public static Object notifyInterceptor(String methodName, Class[] argsClass, Object[] argsValue,
                                           Object proxy)
    {
        FInterceptInfo info = new FInterceptInfo(proxy, methodName, argsClass);

        FProxyInterface proxyInterface = (FProxyInterface) proxy;
        FMethodInterceptor methodInterceptor = proxyInterface.getMethodInterceptor$FProxy$();
        if (methodInterceptor != null)
        {
            return methodInterceptor.intercept(info, argsValue);
        } else
        {
            return executeSuperMethod(methodName, argsClass, argsValue, proxy);
        }
    }

    public static Object executeSuperMethod(String methodName, Class[] argsClass, Object[] argsValue,
                                            Object proxy)
    {
        try
        {
            Method methodSuper = proxy.getClass().getDeclaredMethod(methodName + FProxyInterface.PROXY_CLASS_INVOKE_SUPER_METHOD_SUFFIX,
                    argsClass);
            return methodSuper.invoke(proxy, argsValue);
        } catch (Exception e)
        {
            throw new FProxyException(e);
        }
    }
}
