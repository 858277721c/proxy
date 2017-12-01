package com.fanwe.lib.proxy;

import android.content.Context;

import com.android.dx.Code;
import com.android.dx.Comparison;
import com.android.dx.FieldId;
import com.android.dx.Label;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Administrator on 2017/11/30.
 */
public class FProxy
{
    public static final String TAG = FProxy.class.getSimpleName();

    public static final String DIR_NAME_DEX = "dexProxy";

    private Context mContext;
    private Class mSuperClass;

    public FProxy(Context context)
    {
        mContext = context.getApplicationContext();
    }

    /**
     * 设置要代理的class
     *
     * @param superClass
     */
    public void setSuperClass(Class superClass)
    {
        mSuperClass = superClass;
    }

    public Class getSuperClass()
    {
        return mSuperClass;
    }

    public Object newProxyInstance(FMethodInterceptor methodInterceptor) throws Exception
    {
        final File dirDex = mContext.getExternalFilesDir(DIR_NAME_DEX);

        final DexMakerHelper helper = new DexMakerHelper(mSuperClass);

        makeProxyClass(helper);

        ClassLoader loader = helper.getDexMaker().generateAndLoad(getClass().getClassLoader(), dirDex);
        Class<?> classSub = loader.loadClass(mSuperClass.getName() + FProxyInterface.PROXY_CLASS_SUFFIX);
        Object instance = classSub.newInstance();

        return instance;
    }

    private void makeProxyClass(DexMakerHelper helper)
    {
        // public class com/fanwe/model/Person$FProxy$ extends com/fanwe/model/Person implements FProxyInterface
        helper.declareClass(Modifier.PUBLIC, FProxyInterface.class);

        /**
         * public com/fanwe/model/Person$FProxy$()
         * {
         *     super();
         * }
         */
        Code code = helper.declareConstructor(Modifier.PUBLIC);
        code.invokeDirect(helper.getTypeSuper().getConstructor(), null, helper.getThis(code));
        code.returnVoid();

        // private FMethodInterceptor mMethodInterceptor = null;
        helper.declareField(Modifier.PRIVATE,
                FMethodInterceptor.class, FProxyInterface.FIELD_NAME_METHODINTERCEPTOR, null);
        /**
         * public void setMethodInterceptor$FProxy$(FMethodInterceptor interceptor)
         * {
         *     mMethodInterceptor = handler;
         * }
         */
        code = helper.declareMethod(Modifier.PUBLIC,
                Void.class, FProxyInterface.METHOD_NAME_SETMETHODINTERCEPTOR,
                FMethodInterceptor.class);

        FieldId fieldMethodInterceptor = helper.getField(helper.getTypeSub(),
                FMethodInterceptor.class, FProxyInterface.FIELD_NAME_METHODINTERCEPTOR);

        code.iput(fieldMethodInterceptor,
                helper.getThis(code),
                helper.getParameter(code, 0, FMethodInterceptor.class));
        code.returnVoid();

        final Method[] arrMethod = getSuperClass().getDeclaredMethods();

        String methodName = null;
        Class<?> classReturn = null;
        Class<?> classReturnPack = null;
        Class<?>[] classArgs = null;

        MethodId<?, ?> methodNotifyInterceptor = helper.getMethod(helper.getType(FProxyHelper.class),
                Object.class, FProxyHelper.METHOD_NAME_NOTIFYINTERCEPTOR,
                FMethodInterceptor.class, Class.class, String.class, Class[].class, Object[].class, Object.class);

        for (Method item : arrMethod)
        {
            methodName = item.getName();
            if (methodName.contains("$"))
            {
                continue;
            }
            classReturn = item.getReturnType();
            classArgs = item.getParameterTypes();

            code = helper.declareMethod(item.getModifiers(), classReturn, methodName, classArgs); // 生成方法体

            Local localThis = helper.getThis(code); // 保存当前代理对象

            // ---------- 变量 ----------

            // 保存返回值
            Local localReturn = code.newLocal(helper.getType(classReturn));
            Local localReturnPack = null;
            if (classReturn.isPrimitive())
            {
                classReturnPack = DexMakerHelper.getPackedClass(classReturn);
                localReturnPack = code.newLocal(helper.getType(classReturnPack));
            }

            // Object localObjectReturn;
            Local<Object> localObjectReturn = code.newLocal(TypeId.OBJECT);

            // FMethodInterceptor localMethodInterceptor;
            Local<FMethodInterceptor> localMethodInterceptor = code.newLocal(helper.getType(FMethodInterceptor.class));
            // Class localClass;
            Local<Class> localClass = code.newLocal(helper.getType(Class.class));
            // String localMethodName;
            Local<String> localMethodName = code.newLocal(TypeId.STRING);
            // Class[] localArgsClass;
            Local<Class[]> localArgsClass = code.newLocal(helper.getType(Class[].class));
            // Object[] localArgsValue;
            Local<Object[]> localArgsValue = code.newLocal(helper.getType(Object[].class));

            // Int localIntTmp;
            Local<Integer> localIntTmp = code.newLocal(TypeId.INT);
            // Class localClassTmp;
            Local<Class> localClassTmp = code.newLocal(helper.getType(Class.class));
            Local localObjectTmp = code.newLocal(TypeId.OBJECT);

            // ---------- 变量赋值 ----------
            code.iget(fieldMethodInterceptor, localMethodInterceptor, localThis);

            MethodId methodGetClass = helper.getMethod(helper.getTypeSub(),
                    Class.class, "getClass");
            code.invokeVirtual(methodGetClass, localClass,
                    localThis);

            code.loadConstant(localMethodName, methodName);

            if (classArgs.length > 0)
            {
                code.loadConstant(localIntTmp, classArgs.length);
                code.newArray(localArgsClass, localIntTmp);
                code.newArray(localArgsValue, localIntTmp);

                Class classArg = null;
                for (int i = 0; i < classArgs.length; i++)
                {
                    classArg = classArgs[i];

                    code.loadConstant(localIntTmp, i);
                    code.loadConstant(localClassTmp, classArg);
                    code.aput(localArgsClass, localIntTmp, localClassTmp);

                    if (classArg.isPrimitive())
                    {
                        TypeId typePrimitive = helper.getType(classArg);
                        MethodId methodValueOf = helper.getMethod(typePrimitive,
                                DexMakerHelper.getPackedClass(classArg), "valueOf", classArg);

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

            code.invokeStatic(methodNotifyInterceptor, localObjectReturn,
                    localMethodInterceptor, localClass, localMethodName, localArgsClass, localArgsValue, localThis);

            if (classReturn == Void.class)
            {
                code.returnVoid();
            } else
            {
                if (classReturn.isPrimitive())
                {
                    Label ifNull = new Label();
                    code.loadConstant(localReturnPack, null);
                    code.compare(Comparison.EQ, ifNull, localObjectReturn, localReturnPack);

                    code.cast(localReturn, localObjectReturn);

                    MethodId methodPrimitiveValue = helper.getMethod(helper.getType(classReturnPack),
                            classReturn, classReturn.getSimpleName() + "Value");

                    code.invokeVirtual(methodPrimitiveValue, localReturn, localReturnPack);
                    code.returnValue(localReturn);

                    code.mark(ifNull);
                    code.loadConstant(localReturn, 0);
                    code.returnValue(localReturn);
                } else
                {
                    code.cast(localReturn, localObjectReturn);
                    code.returnValue(localReturn);
                }
            }
        }
    }
}
