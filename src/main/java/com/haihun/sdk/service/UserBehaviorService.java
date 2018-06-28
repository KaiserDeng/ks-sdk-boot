package com.haihun.sdk.service;

import com.haihun.sdk.mapper.UserBehaviorMapper;
import com.haihun.sdk.pojo.UserBehavior;
import com.haihun.sdk.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kaiser·von·d
 * @version 2018/5/10
 */
@Slf4j
@Service
@Transactional(value = "masterTransactionManager", rollbackFor = {Exception.class, RuntimeException.class})
public class UserBehaviorService extends BaseService<UserBehavior> {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;


}
