package com.sd.lib.proxy;

/**
 * 通用异常
 */
public class FProxyException extends RuntimeException
{
    public FProxyException()
    {
    }

    public FProxyException(String message)
    {
        super(message);
    }

    public FProxyException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FProxyException(Throwable cause)
    {
        super(cause);
    }
}
