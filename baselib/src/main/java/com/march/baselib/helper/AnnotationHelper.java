package com.march.baselib.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Project  : CommonLib <p>
 * Package  : com.march.baselib <p>
 * CreateAt : 16/8/17 <p>
 * Describe : 注解操作<p>
 *
 * @author chendong <p>
 */
public class AnnotationHelper {

    /**
     * 获取这个类里所有被某个注解 注解过的方法
     *
     * @param hostCls       被注解的类
     * @param annotationCls 注解
     * @return hostCls中被annotationCls注解过的方法
     */
    public static List<Method> getMethods(Class hostCls, Class<? extends Annotation> annotationCls) {
        List<Method> methods = new ArrayList<>();
        for (Method method : hostCls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationCls)) {
                methods.add(method);
            }
        }
        return methods;
    }

    /**
     * 反射执行这个无参方法
     * @param activity Obj
     * @param executeMethod method
     */
    public static void executeMethod(Object activity, Method executeMethod) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible())
                    executeMethod.setAccessible(true);
                executeMethod.invoke(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
