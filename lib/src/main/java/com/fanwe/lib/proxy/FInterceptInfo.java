package com.fanwe.lib.proxy;

import java.lang.reflect.Method;

/**
 * 拦截到的方法
 */
public class FInterceptInfo
{
    private Object mProxy;
    private String mMethodName;
    private Class[] mArgsClass;

    private Method mMethod;
    private Method mMethodSuper;

    public FInterceptInfo(Object proxy, String methodName, Class[] argsClass)
    {
        mProxy = proxy;
        mMethodName = methodName;
        mArgsClass = argsClass;
    }

    void setMethod(Method method)
    {
        mMethod = method;
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
     * 返回可以调用父类方法的Method
     *
     * @return
     */
    private Method getMethodInvokeSuper()
    {
        try
        {
            if (mMethodSuper == null)
            {
                mMethodSuper = getProxy().getClass().getDeclaredMethod(mMethodName + FProxyInterface.PROXY_CLASS_INVOKE_SUPER_METHOD_SUFFIX,
                        mArgsClass);
            }
            return mMethodSuper;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回拦截到的方法
     *
     * @return
     */
    public Method getMethod()
    {
        try
        {
            if (mMethod == null)
            {
                mMethod = getProxy().getClass().getSuperclass().getDeclaredMethod(mMethodName, mArgsClass);
            }
            return mMethod;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用代理对象父类的方法
     *
     * @param args
     * @return
     */
    public Object invokeSuper(Object[] args)
    {
        try
        {
            return getMethodInvokeSuper().invoke(getProxy(), args);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用某个对象的方法
     *
     * @param object
     * @param args
     * @return
     */
    public Object invokeObject(Object object, Object[] args)
    {
        try
        {
            return getMethod().invoke(object, args);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
