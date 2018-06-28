package com.haihun.sdk.mapper;

import com.haihun.comm.base.BaseMapper;
import com.haihun.sdk.pojo.GameOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GameOrderMapper extends BaseMapper<GameOrder> {


    List<Map<String,Object>> findListByPageAndUserName(@Param("userName") String userName, @Param("page") int page, @Param("limit") Integer limit);
}
