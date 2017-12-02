package com.fanwe.lib.proxy;

import java.lang.reflect.Method;

/**
 * 方法代理
 */
public class FMethodProxy
{
    private Class mProxyClass;
    private String mMethodName;
    private Class[] mArgsClass;

    public FMethodProxy(Class proxyClass, String methodName, Class[] argsClass)
    {
        mProxyClass = proxyClass;
        mMethodName = methodName;
        mArgsClass = argsClass;
    }

    /**
     * 返回可以调用父类方法的Method
     *
     * @return
     */
    public Method getSuperMethod()
    {
        Method method = null;
        try
        {
            method = mProxyClass.getDeclaredMethod(mMethodName + FProxyInterface.PROXY_CLASS_INVOKE_SUPER_METHOD_SUFFIX, mArgsClass);
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * 返回可以调用当前当前类方法的Method
     *
     * @return
     */
    public Method getMethod()
    {
        Method method = null;
        try
        {
            method = mProxyClass.getDeclaredMethod(mMethodName, mArgsClass);
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return method;
    }
}
