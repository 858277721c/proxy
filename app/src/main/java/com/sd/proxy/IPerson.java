package com.sd.proxy;

/**
 * Created by Administrator on 2017/12/2.
 */

public interface IPerson
{
    void getUp();

    float eatFood(String food, int number, float price);

    Person.Book readBook(Person.Book book);

    enum Book
    {
        Chinese,
        English
    }
}
