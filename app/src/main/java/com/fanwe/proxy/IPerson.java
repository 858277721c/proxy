package com.fanwe.proxy;

/**
 * Created by Administrator on 2017/12/2.
 */

public interface IPerson
{
    void getUp();

    float eatFood(String food, int number, float price);

    Person.Book readBooks(Person.Book book);

    enum Book
    {
        Chinese,
        English
    }
}
