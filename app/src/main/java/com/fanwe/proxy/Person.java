package com.fanwe.proxy;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/1.
 */
public class Person
{
    public static final String TAG = Person.class.getSimpleName();

    public void getUp()
    {
        Log.i(TAG, "person getUp");
    }

    public float eatFood(String food, int number, float price)
    {
        Log.i(TAG, "person eatFood:" + food + "," + number + "," + price);
        return number * price;
    }

    public Book readBooks(Book book)
    {
        Log.i(TAG, "person readBooks:" + book);
        return book;
    }

    public Long sleepAt(Long time)
    {
        Log.i(TAG, "person sleepAt:" + time);
        return time;
    }

    public enum Book
    {
        Chinese,
        English
    }
}
