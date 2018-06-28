package com.haihun.comm.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kaiser·von·d
 * @version 2018/5/7
 */
public class BeanUtil {

    /**
     * 复制对象中的可以被编辑的字段
     *
     * @param src    源对象
     * @param target 目标对象
     */
    public static Object copyProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target);
        return target;
    }


    /**
     * 复制对象中的可以被编辑的字段
     *
     * @param src    源对象
     * @param target 目标对象
     * @param clazz  这个类或者接口设置为accessable为true的字段
     */
    public static Object copyProperties(Object src, Object target, Class<?> clazz) {
        BeanUtils.copyProperties(src, target, clazz);
        return target;
    }

    /**
     * 复制对象中的所有属性。并且忽略指定的字段
     *
     * @param src              源对象
     * @param ignoreProperties 忽略字段数组
     * @param target           目标对象
     */
    public static Object copyProperties(Object src, Object target, String... ignoreProperties) {
        BeanUtils.copyProperties(src, target, ignoreProperties);
        return target;
    }

    /**
     * 复制对象中的所有属性。并且忽略Null值
     *
     * @param src    源对象
     * @param target 目标对象
     */
    public static Object copyPropIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
        return target;
    }


    /**
     * 得到对象中所有值为null的字段
     *
     * @param source 源对象
     * @return 值为null的字段数组
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


}
