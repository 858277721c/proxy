package com.fanwe.lib.proxy;

import com.android.dx.Code;
import com.android.dx.DexMaker;
import com.android.dx.FieldId;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * dex帮助类
 */
class DexMakerHelper
{
    private HashMap<Class, TypeId> mMapType = new HashMap<>();

    private DexMaker mDexMaker;
    private Class<?> mSuperClass;

    private final TypeId<?> mTypeSuper;
    private final TypeId<?> mTypeProxy;

    public DexMakerHelper(Class<?> superClass)
    {
        mSuperClass = superClass;

        mTypeSuper = getType(mSuperClass);
        final String typeProxyName = mTypeSuper.getName().replace(";", FProxyInterface.PROXY_CLASS_SUFFIX + ";");
        mTypeProxy = TypeId.get(typeProxyName);
    }

    public Class<?> getSuperClass()
    {
        return mSuperClass;
    }

    public Class<?> getProxyClass()
    {
        return FProxyClass.class;
    }

    public String getProxyClassName()
    {
        return mSuperClass.getName() + FProxyInterface.PROXY_CLASS_SUFFIX;
    }

    public TypeId<?> getTypeSuper()
    {
        return mTypeSuper;
    }

    public TypeId<?> getTypeProxy()
    {
        return mTypeProxy;
    }

    public DexMaker getDexMaker()
    {
        if (mDexMaker == null)
        {
            mDexMaker = new DexMaker();
        }
        return mDexMaker;
    }

    /**
     * 声明类
     *
     * @param flags      权限
     * @param classSuper 父类的class
     * @param interfaces 要实现接口的class
     */
    public void declareClass(int flags, Class<?> classSuper, Class<?>... interfaces)
    {
        TypeId[] arrType = classToTypeId(interfaces);
        if (arrType != null)
        {
            getDexMaker().declare(getTypeProxy(),
                    getTypeProxy().getName(),
                    flags,
                    getType(classSuper),
                    arrType);
        } else
        {
            getDexMaker().declare(getTypeProxy(),
                    getTypeProxy().getName(),
                    flags,
                    getType(classSuper));
        }
    }

    /**
     * 声明构造方法
     *
     * @param flags      权限
     * @param parameters 参数的class
     * @return
     */
    public Code declareConstructor(int flags, Class<?>... parameters)
    {
        MethodId method = getConstructor(getProxyClass(), parameters);
        return getDexMaker().declare(method, flags);
    }

    /**
     * 声明属性
     *
     * @param flags      权限
     * @param classField 属性的class
     * @param fieldName  属性名称
     * @param fieldValue 属性值
     */
    public void declareField(int flags, Class<?> classField, String fieldName, Object fieldValue)
    {
        FieldId field = getField(getProxyClass(), classField, fieldName);
        getDexMaker().declare(field, flags, fieldValue);
    }

    /**
     * 声明方法
     *
     * @param flags       权限
     * @param classReturn 方法返回值的class
     * @param methodName  方法名称
     * @param parameters  方法参数的class
     * @return
     */
    public Code declareMethod(int flags, Class<?> classReturn, String methodName, Class<?>... parameters)
    {
        MethodId method = getMethod(getProxyClass(), classReturn, methodName, parameters);
        return getDexMaker().declare(method, flags);
    }

    public <T> TypeId<T> getType(Class<T> clazz)
    {
        TypeId typeId = mMapType.get(clazz);
        if (typeId == null)
        {
            if (clazz == FProxyClass.class)
            {
                typeId = getTypeProxy();
            } else if (clazz == Void.class)
            {
                typeId = TypeId.VOID;
            } else if (clazz == Object.class)
            {
                typeId = TypeId.OBJECT;
            } else if (clazz == String.class)
            {
                typeId = TypeId.STRING;
            } else
            {
                typeId = TypeId.get(clazz);
            }

            mMapType.put(clazz, typeId);
        }
        return typeId;
    }

    /**
     * 获得属性
     *
     * @param classTarget 目标的class
     * @param classField  属性的class
     * @param fieldName   属性名称
     * @param <T>         目标的class类型
     * @param <F>         属性的class类型
     * @return
     */
    public <T, F> FieldId<T, F> getField(Class<T> classTarget, Class<F> classField, String fieldName)
    {
        TypeId typeTarget = getType(classTarget);
        TypeId typeField = getType(classField);

        return typeTarget.getField(typeField, fieldName);
    }

    /**
     * 获得构造方法
     *
     * @param classTarget 目标的class
     * @param parameters  参数的class
     * @param <T>
     * @return
     */
    public <T> MethodId<T, Void> getConstructor(Class<T> classTarget, Class<?>... parameters)
    {
        TypeId typeTarget = getType(classTarget);
        TypeId[] typeParameters = classToTypeId(parameters);

        if (typeParameters != null)
        {
            return typeTarget.getConstructor(typeParameters);
        } else
        {
            return typeTarget.getConstructor();
        }
    }

    /**
     * 获得方法
     *
     * @param classTarget 目标的class
     * @param classReturn 方法返回值的class
     * @param methodName  方法名称
     * @param parameters  方法参数的class
     * @param <T>         目标的class类型
     * @param <R>         方法返回值的class类型
     * @return
     */
    public <T, R> MethodId<T, R> getMethod(Class<T> classTarget,
                                           Class<R> classReturn, String methodName, Class<?>... parameters)
    {
        TypeId typeTarget = getType(classTarget);
        TypeId typeReturn = getType(classReturn);
        TypeId[] typeParameters = classToTypeId(parameters);

        if (typeParameters != null)
        {
            return typeTarget.getMethod(typeReturn, methodName, typeParameters);
        } else
        {
            return typeTarget.getMethod(typeReturn, methodName);
        }
    }

    /**
     * 返回基本类型的valueOf方法<br>
     * 比如：Integer.valueOf(1);
     *
     * @param clazz 基本类型的class
     * @return
     */
    public MethodId<?, ?> getMethodPrimitiveValueOf(Class<?> clazz)
    {
        Class classPack = getPackedClass(clazz);

        return getMethod(classPack,
                classPack,
                "valueOf",
                clazz);
    }

    /**
     * 返回基本类型的Value方法<br>
     * 比如：integer.intValue();
     *
     * @param clazz
     * @return
     */
    public MethodId<?, ?> getMethodPrimitiveValue(Class<?> clazz)
    {
        Class classPack = getPackedClass(clazz);

        return getMethod(classPack,
                clazz,
                clazz.getSimpleName() + "Value");
    }

    /**
     * 获得当前对象this
     *
     * @param code
     * @return
     */
    public Local getThis(Code code)
    {
        return code.getThis(getTypeProxy());
    }

    /**
     * 获得参数
     *
     * @param code
     * @param index 第几个参数
     * @param clazz 参数的class
     * @param <T>   参数的class类型
     * @return
     */
    public <T> Local<T> getParameter(Code code, int index, Class<T> clazz)
    {
        TypeId type = getType(clazz);
        return code.getParameter(index, type);
    }

    /**
     * 创建临时变量
     *
     * @param code
     * @param clazz 变量的class
     * @param <T>   变量的class类型
     * @return
     */
    public <T> Local<T> newLocal(Code code, Class<T> clazz)
    {
        TypeId type = getType(clazz);
        return code.newLocal(type);
    }

    /**
     * 声明所有父类支持的合法构造方法
     *
     * @throws Exception
     */
    public void declareConstructors() throws Exception
    {
        Constructor[] arrConstructor = getSuperClass().getDeclaredConstructors();

        int modifiers = 0;
        Class[] classArgs = null;
        boolean foundConstructor = false;
        for (Constructor item : arrConstructor)
        {
            modifiers = item.getModifiers();
            if (Modifier.isPrivate(modifiers) || modifiers == 0)
            {
                continue;
            }
            foundConstructor = true;
            classArgs = item.getParameterTypes();

            Code code = declareConstructor(modifiers, classArgs);
            code.invokeDirect(getConstructor(getSuperClass(), classArgs), null, getThis(code));
            code.returnVoid();
        }

        if (!foundConstructor)
        {
            throw new FProxyException("cant find legal Constructor");
        }
    }


    public TypeId<?>[] classToTypeId(Class<?>[] arrClass)
    {
        if (arrClass == null || arrClass.length <= 0)
        {
            return null;
        }

        TypeId<?>[] arrResult = new TypeId<?>[arrClass.length];
        for (int i = 0; i < arrClass.length; i++)
        {
            arrResult[i] = getType(arrClass[i]);
        }
        return arrResult;
    }

    /**
     * 返回基本类型的包装类
     *
     * @param primitive
     * @return
     */
    public static Class<?> getPackedClass(Class<?> primitive)
    {
        if (primitive == boolean.class)
        {
            return Boolean.class;
        } else if (primitive == byte.class)
        {
            return Byte.class;
        } else if (primitive == char.class)
        {
            return Character.class;
        } else if (primitive == double.class)
        {
            return Double.class;
        } else if (primitive == float.class)
        {
            return Float.class;
        } else if (primitive == int.class)
        {
            return Integer.class;
        } else if (primitive == long.class)
        {
            return Long.class;
        } else if (primitive == short.class)
        {
            return Short.class;
        } else
        {
            return primitive;
        }
    }

    /**
     * 由于创建代理的时候无法获得代理对象的class<br>
     * 所以当传入的对象class为这个类的class的时候会被当做代理对象的class
     */
    private class FProxyClass
    {
    }
}
