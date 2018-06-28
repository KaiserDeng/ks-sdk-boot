package com.haihun.sdk.mapper;


import com.haihun.comm.base.BaseMapper;
import com.haihun.sdk.pojo.User;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotBlank;

public interface UserMapper extends BaseMapper<User> {


    User findByMobile(@Param("mobile") String mobile);

    User findByUserName(@Param("userName") String userName);

    User findUserByWeChatIdorSinaId(User user);

    User findUserByUserNameAndMobile(@Param("userName") String userName, @Param("mobile") String mobile);

    Integer updateMobile(@Param("mobile") String mobile, @Param("id") Integer id);


    User findUserByUserNameOrMobile(@NotBlank(message = "账号不能为空！") @Param("userName") String userName);
}