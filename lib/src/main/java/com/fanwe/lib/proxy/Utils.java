package com.fanwe.lib.proxy;

import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.lang.reflect.Method;

/**
 * Created by zhengjun on 2017/12/1.
 */

class Utils
{
    public static MethodId<?, ?> getMethodId(TypeId<?> typeId, Method method)
    {
        String methodName = method.getName();
        if (methodName.contains("$"))
        {
            return null;
        }

        MethodId<?, ?> methodId = null;

        Class<?> classReturn = method.getReturnType();
        Class<?>[] arrClassParam = method.getParameterTypes();
        TypeId<?>[] arrTypeParam = classToTypeId(arrClassParam);
        if (arrTypeParam != null)
        {
            methodId = typeId.getMethod(TypeId.get(classReturn), methodName, arrTypeParam);
        } else
        {
            methodId = typeId.getMethod(TypeId.get(classReturn), methodName);
        }
        return methodId;
    }

    public static TypeId<?>[] classToTypeId(Class<?>[] arrClass)
    {
        if (arrClass == null || arrClass.length <= 0)
        {
            return null;
        }

        TypeId<?>[] arrResult = new TypeId<?>[arrClass.length];
        for (int i = 0; i < arrClass.length; i++)
        {
            arrResult[i] = TypeId.get(arrClass[i]);
        }
        return arrResult;
    }

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

    public static String getPrimitiveValueMethodName(Class primitive)
    {
        if (primitive == boolean.class)
        {
            return "booleanValue";
        } else if (primitive == byte.class)
        {
            return "byteValue";
        } else if (primitive == char.class)
        {
            return "charValue";
        } else if (primitive == double.class)
        {
            return "doubleValue";
        } else if (primitive == float.class)
        {
            return "floatValue";
        } else if (primitive == int.class)
        {
            return "intValue";
        } else if (primitive == long.class)
        {
            return "longValue";
        } else if (primitive == short.class)
        {
            return "shortValue";
        } else
        {
            throw new RuntimeException(primitive.getName() + " dit not primitive class");
        }
    }
}
