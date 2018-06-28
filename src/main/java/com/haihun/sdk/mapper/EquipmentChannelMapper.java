package com.haihun.sdk.mapper;

import com.haihun.comm.base.BaseMapper;
import com.haihun.sdk.pojo.EquipmentChannel;
import org.apache.ibatis.annotations.Param;

public interface EquipmentChannelMapper extends BaseMapper<EquipmentChannel> {

    EquipmentChannel findByEcId(@Param("duid") String duid);

    EquipmentChannel findByDuidAndAppIdChannelId(@Param("duid") String duid, @Param("appKey") String appKey, @Param("channel") Integer channel);

    Integer findActive(@Param("ecId") String ecId);

    void addActiveStatus(@Param("ecId") String ecId);


}