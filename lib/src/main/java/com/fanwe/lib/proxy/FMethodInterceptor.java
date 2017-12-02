package com.fanwe.lib.proxy;

/**
 * 方法拦截接口
 */
public interface FMethodInterceptor
{
    /**
     * 方法拦截回调
     *
     * @param info 拦截信息
     * @param args 方法参数
     * @return
     */
    Object intercept(FInterceptInfo info, Object[] args);
}
