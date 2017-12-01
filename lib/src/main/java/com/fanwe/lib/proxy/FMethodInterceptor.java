package com.fanwe.lib.proxy;

/**
 * Created by Administrator on 2017/12/1.
 */

public interface FMethodInterceptor
{
    Object intercept(Object proxy, Object[] args, FMethodProxy methodProxy);
}
