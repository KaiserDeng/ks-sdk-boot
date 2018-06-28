package com.haihun.sdk.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 *@author 19187
 *@version 2018-06-15
 */
@Data
@Table(name ="tb_sys_permission")
public class Permission {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 权限名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 权限资源路径
     */
    @Column(name = "url")
    private String url;

    /**
     * 权限描述
     */
    @Column(name = "description")
    private String description;
}