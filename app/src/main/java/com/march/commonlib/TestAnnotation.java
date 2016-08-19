package com.march.commonlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.march.commonlib
 * CommonLib
 * Created by chendong on 16/8/17.
 * Copyright © 2016年 chendong. All rights reserved.
 * Desc :
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface TestAnnotation {
    int testCode();
    String[] value();
}
