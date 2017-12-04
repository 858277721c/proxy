package com.fanwe.proxy;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/1.
 */
public class Person implements IPerson
{
    public static final String TAG = Person.class.getSimpleName();

    private String name = TAG;
    private int age = 20;

    public Person()
    {
    }

    public Person(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    @Override
    public void getUp()
    {
        Log.i(TAG, name + "," + age + " getUp");
    }

    @Override
    public float eatFood(String food, int number, float price)
    {
        Log.i(TAG, name + "," + age + " eatFood:" + food + "," + number + "," + price);
        return number * price;
    }

    @Override
    public Book readBook(Book book)
    {
        Log.i(TAG, name + "," + age + " readBook:" + book);
        return book;
    }
}
