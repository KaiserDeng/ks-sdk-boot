package com.haihun.annotation;

import java.lang.annotation.*;

/**
 * 被该注解修饰的字段在生成swagger文档时将被反射拦截
 * 美化 @ApiModel的example 属性，使其json格式化
 *
 * @author kaiser·von·d
 * @version 2018/6/4
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExampleFormat {

}
