package com.haihun.comm.base;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用mapper
 *
 * @author kaiser·von·d
 * @version 2018/4/20
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {


}
