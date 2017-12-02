package com.fanwe.lib.proxy;

import java.lang.reflect.Method;

/**
 * 拦截到的方法
 */
public class FInterceptInfo
{
    private Object mProxy;
    private Class mProxyClass;
    private String mMethodName;
    private Class[] mArgsClass;

    public FInterceptInfo(Object proxy, String methodName, Class[] argsClass)
    {
        mProxy = proxy;
        mProxyClass = proxy.getClass();
        mMethodName = methodName;
        mArgsClass = argsClass;
    }

    /**
     * 返回可以调用父类方法的Method
     *
     * @return
     */
    private Method getSuperMethod()
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
     * 返回当前拦截到的方法
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

    /**
     * 返回代理
     *
     * @return
     */
    public Object getProxy()
    {
        return mProxy;
    }

    /**
     * 调用代理的父类方法
     *
     * @param args
     * @return
     * @throws Exception
     */
    public Object invokeSuper(Object[] args) throws Exception
    {
        return getSuperMethod().invoke(mProxy, args);
    }

    /**
     * 调用某个对象的方法
     *
     * @param object
     * @param args
     * @return
     * @throws Exception
     */
    public Object invokeObject(Object object, Object[] args) throws Exception
    {
        return getMethod().invoke(object, args);
    }
}
