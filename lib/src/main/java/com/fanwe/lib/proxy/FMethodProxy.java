package com.fanwe.lib.proxy;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/12/1.
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
     * @throws NoSuchMethodException
     */
    public Method getSuperMethod() throws NoSuchMethodException
    {
        Method method = mProxyClass.getDeclaredMethod(mMethodName + FProxy.PROXY_CLASS_INVOKE_SUPER_METHOD_SUFFIX,
                mArgsClass);
        return method;
    }

    /**
     * 返回可以调用当前当前累方法的Method
     *
     * @return
     * @throws NoSuchMethodException
     */
    public Method getMethod() throws NoSuchMethodException
    {
        Method method = mProxyClass.getDeclaredMethod(mMethodName,
                mArgsClass);
        return method;
    }
}
