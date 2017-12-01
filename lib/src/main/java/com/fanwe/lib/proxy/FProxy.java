package com.fanwe.lib.proxy;

import android.content.Context;

import com.android.dx.Code;
import com.android.dx.DexMaker;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.io.File;
import java.lang.reflect.InvocationHandler;
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
    private Class mTargetClass;
    private DexMaker mDexMaker;

    public FProxy(Context context)
    {
        mContext = context.getApplicationContext();
    }

    /**
     * 设置要代理的class
     *
     * @param targetClass
     */
    public void setTargetClass(Class targetClass)
    {
        mTargetClass = targetClass;
    }

    public Class getTargetClass()
    {
        return mTargetClass;
    }

    private DexMaker getDexMaker()
    {
        if (mDexMaker == null)
        {
            mDexMaker = new DexMaker();
        }
        return mDexMaker;
    }

    public Object newProxyInstance(FMethodInterceptor methodInterceptor) throws Exception
    {
        final File dirDex = mContext.getExternalFilesDir(DIR_NAME_DEX);

        final DexMakerHelper helper = new DexMakerHelper(mTargetClass);

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
                FMethodInterceptor.class, "mMethodInterceptor",
                null);
        /**
         * public void setInvocationHandler$SDProxy$(InvocationHandler handler)
         * {
         *     mInvocationHandler = handler;
         * }
         */

        code = helper.declareMethod(Modifier.PUBLIC,
                Void.class, FProxyInterface.METHOD_NAME_SETMETHODINTERCEPTOR,
                FMethodInterceptor.class);

        code.iput(fieldMethodInterceptor, localThis, code.getParameter(0, typeMethodInterceptor));
        code.returnVoid();

        final Method[] arrMethod = getTargetClass().getDeclaredMethods();

        int paramCount = 0;
        Class<?> classReturn = null;

        MethodId<?, ?> methodSub = null;
        MethodId<?, ?> methodInvoke = Utils.getMethodId(typeMethodInterceptor,
                InvocationHandler.class.getDeclaredMethod("invoke", Object.class, Method.class, Object[].class));

        TypeId<?> typeReturn = null;
        TypeId<Object[]> typeArrObject = TypeId.get(Object[].class);
        for (Method item : arrMethod)
        {
            methodSub = Utils.getMethodId(typeSub, item);
            if (methodSub == null)
            {
                continue;
            }
            paramCount = item.getParameterTypes().length;
            classReturn = item.getReturnType();
            typeReturn = TypeId.get(classReturn);

            code = getDexMaker().declare(methodSub, item.getModifiers());  // 生成方法体

            Local<?> localReturn = code.newLocal(typeReturn);
            Local<Object> localInvokeResult = code.newLocal(TypeId.OBJECT); // Object invokeResult;
            Local<Method> localMethod = code.newLocal(typeMethod); // Method method;
            Local<Integer> localParam = code.newLocal(TypeId.INT);
            Local<Object[]> localArrParam = code.newLocal(typeArrObject); // Object[] arrParam = new Object[paramCount];

            localThis = code.getThis(typeSub);

            if (paramCount > 0)
            {
//                code.loadConstant(localMethod, item);

                TypeId<?>[] arrType = Utils.classToTypeId(item.getParameterTypes());
                Object[] arrParam = new Object[paramCount];
                for (int i = 0; i < paramCount; i++)
                {
                    arrParam[i] = code.getParameter(i, arrType[i]);
                }
                code.loadConstant(localArrParam, arrParam);

                code.invokeVirtual(methodInvoke, localInvokeResult, localThis,
                        localThis, localMethod, localArrParam);
            } else
            {
                code.invokeVirtual(methodInvoke, localInvokeResult, localThis);
            }

            if (classReturn != Void.class)
            {
                if (classReturn.isPrimitive())
                {

                } else
                {
                    code.cast(localReturn, localInvokeResult);
                    code.returnValue(localReturn);
                }
            } else
            {
                code.returnVoid();
            }
        }

        ClassLoader loader = getDexMaker().generateAndLoad(getClass().getClassLoader(), dirDex);
        Class<?> classSub = loader.loadClass(mTargetClass.getName() + ISDProxy.PROXY_CLASS_SUFFIX);
        Object instance = classSub.newInstance();

        return instance;
    }
}
