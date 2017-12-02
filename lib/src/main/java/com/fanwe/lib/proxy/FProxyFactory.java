package com.fanwe.lib.proxy;

import android.content.Context;

import com.android.dx.Code;
import com.android.dx.Comparison;
import com.android.dx.FieldId;
import com.android.dx.Label;
import com.android.dx.Local;
import com.android.dx.MethodId;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 代理工厂
 */
public class FProxyFactory
{
    public static final String DIR_NAME_DEX = "dexfiles";

    private Context mContext;

    public FProxyFactory(Context context)
    {
        mContext = context.getApplicationContext();
    }

    private File getDexDir()
    {
        return mContext.getDir(DIR_NAME_DEX, Context.MODE_PRIVATE);
    }

    public <T> T newProxy(Class<T> clazz, FMethodInterceptor methodInterceptor) throws Exception
    {
        DexMakerHelper helper = new DexMakerHelper(clazz);
        makeProxyClass(helper);

        ClassLoader loader = helper.getDexMaker().generateAndLoad(getClass().getClassLoader(), getDexDir());
        Class<?> classProxy = loader.loadClass(helper.getProxyClassName());

        FProxyInterface instance = (FProxyInterface) classProxy.newInstance();
        instance.setMethodInterceptor$FProxy$(methodInterceptor);
        return (T) instance;
    }

    private void makeProxyClass(DexMakerHelper helper)
    {
        // public class com/fanwe/model/Person$FProxy$ extends com/fanwe/model/Person implements FProxyInterface
        helper.declareClass(Modifier.PUBLIC, FProxyInterface.class);

        // ---------- 构造方法start ----------
        /**
         * public com/fanwe/model/Person$FProxy$()
         * {
         *     super();
         * }
         */
        Code code = helper.declareConstructor(Modifier.PUBLIC);
        code.invokeDirect(helper.getTypeSuper().getConstructor(), null, helper.getThis(code));
        code.returnVoid();
        // ---------- 构造方法end ----------

        // ---------- 属性start ----------
        // private FMethodInterceptor mMethodInterceptor = null;
        helper.declareField(Modifier.PRIVATE,
                FMethodInterceptor.class, FProxyInterface.FIELD_NAME_METHODINTERCEPTOR, null);
        // ---------- 属性end ----------

        // ---------- FProxyInterface接口方法start ----------
        /**
         * public void setMethodInterceptor$FProxy$(FMethodInterceptor interceptor)
         * {
         *     mMethodInterceptor = handler;
         * }
         */
        code = helper.declareMethod(Modifier.PUBLIC,
                Void.class, FProxyInterface.METHOD_NAME_SETMETHODINTERCEPTOR,
                FMethodInterceptor.class);

        FieldId fieldMethodInterceptor = helper.getField(helper.getSubClass(),
                FMethodInterceptor.class, FProxyInterface.FIELD_NAME_METHODINTERCEPTOR);

        code.iput(fieldMethodInterceptor,
                helper.getThis(code),
                helper.getParameter(code, 0, FMethodInterceptor.class));
        code.returnVoid();

        /**
         * public FMethodInterceptor getMethodInterceptor$FProxy$()
         * {
         *     return this.mMethodInterceptor;
         * }
         */
        code = helper.declareMethod(Modifier.PUBLIC,
                FMethodInterceptor.class, FProxyInterface.METHOD_NAME_GETMETHODINTERCEPTOR);

        Local<FMethodInterceptor> localMethodInterceptor = helper.newLocal(code, FMethodInterceptor.class);
        code.iget(fieldMethodInterceptor, localMethodInterceptor, helper.getThis(code));
        code.returnValue(localMethodInterceptor);

        // ---------- FProxyInterface接口方法end ----------

        final Method[] arrMethod = helper.getSuperClass().getDeclaredMethods();

        String methodName = null;
        String methodNameSuper = null;
        Class<?> classReturn = null;
        Class<?> classReturnPack = null;
        boolean isReturnVoid = false;
        Class<?>[] classArgs = null;

        MethodId<?, ?> methodNotifyInterceptor = helper.getMethod(FProxyHelper.class,
                Object.class, FProxyHelper.METHOD_NAME_NOTIFYINTERCEPTOR,
                String.class, Class[].class, Object[].class, Object.class);

        for (Method item : arrMethod)
        {
            methodName = item.getName();
            if (methodName.contains("$"))
            {
                continue;
            }
            classReturn = item.getReturnType();
            isReturnVoid = classReturn.getSimpleName().equals("void");
            classArgs = item.getParameterTypes();

            code = helper.declareMethod(item.getModifiers(), classReturn, methodName, classArgs); // 生成方法体

            // ---------- 变量 ----------

            // 保存返回值
            Local localReturn = helper.newLocal(code, classReturn);
            Local localReturnPack = null;
            if (classReturn.isPrimitive())
            {
                classReturnPack = DexMakerHelper.getPackedClass(classReturn);
                localReturnPack = helper.newLocal(code, classReturnPack);
            }

            Local<Object> localReturnObject = helper.newLocal(code, Object.class);

            Local<String> localMethodName = helper.newLocal(code, String.class);
            Local<Class[]> localArgsClass = helper.newLocal(code, Class[].class);
            Local<Object[]> localArgsValue = helper.newLocal(code, Object[].class);

            Local<Integer> localIntTmp = helper.newLocal(code, int.class);
            Local<Class> localClassTmp = helper.newLocal(code, Class.class);
            Local<Object> localObjectTmp = helper.newLocal(code, Object.class);

            // ---------- 变量赋值 ----------
            code.loadConstant(localMethodName, methodName);

            if (classArgs.length > 0)
            {
                code.loadConstant(localIntTmp, classArgs.length);
                code.newArray(localArgsClass, localIntTmp);
                code.newArray(localArgsValue, localIntTmp);

                Class<?> classArg = null;
                for (int i = 0; i < classArgs.length; i++)
                {
                    classArg = classArgs[i];

                    code.loadConstant(localIntTmp, i);
                    code.loadConstant(localClassTmp, classArg);
                    code.aput(localArgsClass, localIntTmp, localClassTmp);

                    if (classArg.isPrimitive())
                    {
                        Class<?> classArgPack = DexMakerHelper.getPackedClass(classArg);

                        MethodId methodValueOf = helper.getMethod(classArgPack,
                                classArgPack, "valueOf", classArg);

                        code.invokeStatic(methodValueOf, localObjectTmp,
                                helper.getParameter(code, i, classArg));

                        code.aput(localArgsValue, localIntTmp, localObjectTmp);
                    } else
                    {
                        code.aput(localArgsValue, localIntTmp, helper.getParameter(code, i, classArg));
                    }
                }
            } else
            {
                code.loadConstant(localArgsClass, null);
                code.loadConstant(localArgsValue, null);
            }

            // 调用拦截对象
            code.invokeStatic(methodNotifyInterceptor, isReturnVoid ? null : localReturnObject,
                    localMethodName, localArgsClass, localArgsValue, helper.getThis(code));

            if (isReturnVoid)
            {
                code.returnVoid();
            } else
            {
                if (classReturn.isPrimitive())
                {
                    Label ifNull = new Label();
                    code.loadConstant(localReturnPack, null);
                    code.compare(Comparison.EQ, ifNull, localReturnObject, localReturnPack);

                    code.cast(localReturnPack, localReturnObject);

                    MethodId methodPrimitiveValue = helper.getMethod(classReturnPack,
                            classReturn, classReturn.getSimpleName() + "Value");

                    code.invokeVirtual(methodPrimitiveValue, localReturn, localReturnPack);
                    code.returnValue(localReturn);

                    code.mark(ifNull);
                    code.loadConstant(localReturn, 0);
                    code.returnValue(localReturn);
                } else
                {
                    code.cast(localReturn, localReturnObject);
                    code.returnValue(localReturn);
                }
            }

            // 创建调用父类的方法
            methodNameSuper = methodName + FProxyInterface.PROXY_CLASS_INVOKE_SUPER_METHOD_SUFFIX;
            code = helper.declareMethod(item.getModifiers(), classReturn, methodNameSuper, classArgs);

            localReturn = helper.newLocal(code, classReturn);
            Local[] localSuperArgsValue = null;

            MethodId methodSuper = helper.getMethod(helper.getSuperClass(), classReturn, methodName, classArgs);

            if (classArgs.length > 0)
            {
                localSuperArgsValue = new Local[classArgs.length];
                for (int i = 0; i < classArgs.length; i++)
                {
                    localSuperArgsValue[i] = helper.getParameter(code, i, classArgs[i]);
                }

                code.invokeSuper(methodSuper, isReturnVoid ? null : localReturn, helper.getThis(code),
                        localSuperArgsValue);
            } else
            {
                code.invokeSuper(methodSuper, isReturnVoid ? null : localReturn, helper.getThis(code));
            }

            if (isReturnVoid)
            {
                code.returnVoid();
            } else
            {
                code.returnValue(localReturn);
            }
        }
    }
}
