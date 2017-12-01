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
                    return "modify result";
                }
            });
            Object result = person.eat("apple");
            Log.i(TAG, String.valueOf(result));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
