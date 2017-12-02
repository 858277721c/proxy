package com.fanwe.proxy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fanwe.lib.proxy.FInterceptInfo;
import com.fanwe.lib.proxy.FMethodInterceptor;
import com.fanwe.lib.proxy.FProxyFactory;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private FProxyFactory mProxyFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            mProxyFactory = new FProxyFactory(this); // 创建代理工厂
            Person person = mProxyFactory.newProxy(Person.class, new FMethodInterceptor() // 创建代理对象
            {
                @Override
                public Object intercept(FInterceptInfo info, Object[] args)
                {
                    try
                    {
                        // 拦截到代理方法被执行
                        String methodName = info.getMethod().getName(); // 被拦截的方法名称
                        Log.i(TAG, "intercept method---------->" + methodName);
                        Object result = info.invokeSuper(args); //调用代理对象父类的方法
                        return result;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            // ---------- 测试调用代理的方法 ----------
            person.getUp();
            float money = person.eatFood("banana", 1, 1.5f);
            Person.Book book = person.readBooks(Person.Book.Chinese);
            Long time = person.sleepAt(System.currentTimeMillis());

            Log.i(TAG, String.valueOf(money) + "," + book + "," + time);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mProxyFactory.clearDexFiles(); // 清空所有保存本地的代理class
    }
}
