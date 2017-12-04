package com.fanwe.proxy;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/1.
 */
public class Person implements IPerson
{
    public static final String TAG = Person.class.getSimpleName();

    private String name = TAG;

    public Person()
    {
    }

    public Person(String name)
    {
        this.name = name;
    }

    @Override
    public void getUp()
    {
        Log.i(TAG, name + " getUp");
    }

    @Override
    public float eatFood(String food, int number, float price)
    {
        Log.i(TAG, name + " eatFood:" + food + "," + number + "," + price);
        return number * price;
    }

    @Override
    public Book readBook(Book book)
    {
        Log.i(TAG, name + " readBook:" + book);
        return book;
    }
}
