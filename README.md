## proxy
Android动态创建代理对象<br>
* 接口的代理->内部采用java原生方式实现
* 普通类的代理->内部采用[dexmaker](https://github.com/linkedin/dexmaker)实现

## Gradle
[![](https://jitpack.io/v/zj565061763/proxy.svg)](https://jitpack.io/#zj565061763/proxy)

## 使用方法
```java
public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private FProxyFactory mProxyFactory;
    private Person mPerson = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            mProxyFactory = new FProxyFactory(this); // 创建代理工厂
            Person proxy = mProxyFactory.newProxy(Person.class, new FMethodInterceptor() // 创建代理对象
            {
                @Override
                public Object intercept(FInterceptInfo info, Object[] args)
                {
                    try
                    {
                        // 拦截到代理方法被执行
                        String methodName = info.getMethod().getName(); // 被拦截的方法名称
                        Log.i(TAG, "intercept method---------->" + methodName);
                        Object result = info.invokeSuper(args); // 调用代理对象父类的方法
                        info.getMethod().invoke(mPerson, args); // 触发mPerson对象的方法
                        return result;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            // ---------- 测试调用代理的方法 ----------
            proxy.getUp();
            float money = proxy.eatFood("banana", 1, 1.5f);
            Person.Book book = proxy.readBook(Person.Book.Chinese);
            Log.i(TAG, String.valueOf(money) + "," + book);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mProxyFactory.clearDexFiles(); // 清空所有保存本地的代理class
    }
}
```