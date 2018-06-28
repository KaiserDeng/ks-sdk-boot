package com.haihun.sdk.service;

import com.alipay.api.domain.UserDetails;
import com.haihun.sdk.mapper.UserMapper;
import com.haihun.sdk.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kaiser·von·d
 * @version 2018/6/14
 */
@Service
public class UserPermissionService{

    @Autowired
    private UserMapper userMapper;

//    @Override
    public UserDetails loadUserByUsername(String username)  {
        User user = userMapper.findByUserName(username);

        return null;

    }
}
