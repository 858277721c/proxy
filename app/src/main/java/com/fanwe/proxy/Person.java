package com.fanwe.proxy;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/1.
 */
public class Person implements IPerson
{
    public static final String TAG = Person.class.getSimpleName();

    @Override
    public void getUp()
    {
        Log.i(TAG, "getUp");
    }

    @Override
    public float eatFood(String food, int number, float price)
    {
        Log.i(TAG, "eatFood:" + food + "," + number + "," + price);
        return number * price;
    }

    @Override
    public Book readBook(Book book)
    {
        Log.i(TAG, "readBook:" + book);
        return book;
    }
}
