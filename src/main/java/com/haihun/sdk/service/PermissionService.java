package com.haihun.sdk.service;

import com.haihun.sdk.mapper.PermissionMapper;
import com.haihun.sdk.pojo.Permission;
import com.haihun.sdk.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author kaiser·von·d
 * @version 2018/6/15
 */
public class PermissionService extends BaseService<Permission> {

    @Autowired
    private PermissionMapper mapper;

}
