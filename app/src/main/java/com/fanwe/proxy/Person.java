package com.fanwe.proxy;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/1.
 */

public class Person
{

    public static final String TAG = Person.class.getSimpleName();

    public int eat(String food)
    {
        Log.i(TAG, "eat:" + food + "," + 10);
        return 10;
    }

}
