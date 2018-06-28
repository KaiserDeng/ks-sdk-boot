package com.haihun.sdk.mapper;

import com.haihun.comm.base.BaseMapper;
import com.haihun.sdk.pojo.Equipment;
import org.apache.ibatis.annotations.Param;

public interface EquipmentMapper extends BaseMapper<Equipment> {

    Equipment findByDuid(@Param("duid") String duid);
}