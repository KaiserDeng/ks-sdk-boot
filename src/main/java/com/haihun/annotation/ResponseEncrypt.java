package com.haihun.annotation;

import java.lang.annotation.*;

/**
 * 响应加密标识注解
 *
 * @author kaiser·von·d
 * @version 2018/5/3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseEncrypt {


    boolean value() default true;

}


