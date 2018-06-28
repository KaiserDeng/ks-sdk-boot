package com.haihun.comm.util;

import com.haihun.comm.constant.CodeConst;
import com.haihun.sdk.vo.result.Result;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;


/**
 * 实体类字段验证工具类
 *
 * @author kaiser·von·d
 * @version 2018/4/26
 */
public class ValidateUtil {

    public static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    /**
     * 验证实体类，并封装成一个Result及其子类对象
     *
     * @param t   实体类
     * @param <T> 泛型方法
     * @return 错误信息
     */
    public static <T> Result validate(T t) throws Exception {
        return validate(t, Result.class);
    }

    public static <T, U> U validate(T t, Class<? extends Result> clazz) throws Exception {
        String errorMsg = validateError(t);
        if (StringUtils.isNotBlank(errorMsg)) {
            return (U) clazz.getDeclaredConstructor(int.class, String.class, String.class).newInstance(CodeConst.WARN.getValue(), errorMsg, null);
        }
        return (U) clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * 验证实体类，并返回错误信息
     *
     * @param t   实体类
     * @param <T> 泛型方法
     * @return 错误信息
     */
    public static <T> String validateError(T t) {
        Set<ConstraintViolation<T>> set = validator.validate(t);
        String errorMsg = "";
        if (set.size() > 0) {
            int i = 1;
            for (ConstraintViolation<T> cv : set) {
                errorMsg += cv.getPropertyPath() + " : " + cv.getMessage();
                if (i != set.size()) {
                    errorMsg += "，";
                }
            }
        }
        return errorMsg;
    }

}
