package com.fanwe.proxy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fanwe.lib.proxy.FMethodInterceptor;
import com.fanwe.lib.proxy.FMethodProxy;
import com.fanwe.lib.proxy.FProxy;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            FProxy proxy = new FProxy(this);
            proxy.setSuperClass(Person.class);
            Person person = (Person) proxy.newProxyInstance(new FMethodInterceptor()
            {
                @Override
                public Object intercept(Object proxy, Object[] args, FMethodProxy methodProxy)
                {
                    try
                    {
                        return methodProxy.getSuperMethod().invoke(proxy, args);
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
}
