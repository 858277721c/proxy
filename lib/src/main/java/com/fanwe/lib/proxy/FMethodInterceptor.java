package com.fanwe.lib.proxy;

/**
 * 方法拦截接口
 */
public interface FMethodInterceptor
{
    /**
     * 方法拦截回调
     *
     * @param proxy       代理对象
     * @param args        方法参数值数组
     * @param methodInfo 方法代理
     * @return
     */
    Object intercept(Object proxy, Object[] args, FMethodInfo methodInfo);
}
