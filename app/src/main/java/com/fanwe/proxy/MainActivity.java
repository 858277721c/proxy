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

        mProxyFactory = new FProxyFactory(this);

        try
        {
            Person person = mProxyFactory.newProxy(Person.class, new FMethodInterceptor()
            {
                @Override
                public Object intercept(FInterceptInfo info, Object[] args)
                {
                    try
                    {
                        Log.i(TAG, "before method---------->" + info.getMethod().getName());
                        Object result = info.invokeSuper(args);
                        Log.e(TAG, "after method");
                        return result;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            person.getUp();
            float money = person.eat("apple", 100, 1.1f);
            boolean bReadBook = person.readBook("person");
            Person.Language language = person.learnLanguage(Person.Language.Chinese);
            Long time = person.sleepAt(System.currentTimeMillis());

            Log.i(TAG, String.valueOf(money) + "," + bReadBook + "," + language + "," + time);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mProxyFactory.clearDexFiles();
    }
}
