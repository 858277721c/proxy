package com.fanwe.proxy;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/1.
 */

public class Person
{
    public static final String TAG = Person.class.getSimpleName();

    /**
     * 起床
     */
    public void getUp()
    {
        Log.i(TAG, "person get up");
    }

    /**
     * 吃东西
     *
     * @param food
     * @param number
     * @param price
     * @return
     */
    public float eat(String food, int number, float price)
    {
        Log.i(TAG, "eat:" + food + "," + number + "," + price);
        return number * price;
    }

    /**
     * 读书
     *
     * @param book
     * @return
     */
    public boolean readBook(String book)
    {
        Log.i(TAG, "person readBook:" + book);
        return true;
    }

    /**
     * 学习语言
     *
     * @param language
     * @return
     */
    public Language learnLanguage(Language language)
    {
        Log.i(TAG, "person learnLanguage:" + language);
        return language;
    }

    /**
     * 睡觉
     *
     * @param time
     * @return
     */
    public Long sleepAt(Long time)
    {
        Log.i(TAG, "person sleepAt:" + time);
        return time;
    }

    public enum Language
    {
        Chinese,
        English
    }
}
