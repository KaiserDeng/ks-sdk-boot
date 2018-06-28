package com.haihun.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.haihun.comm.util.BeanUtil;
import com.haihun.sdk.mapper.EquipmentChannelMapper;
import com.haihun.sdk.mapper.EquipmentMapper;
import com.haihun.sdk.pojo.Equipment;
import com.haihun.sdk.pojo.EquipmentChannel;
import com.haihun.sdk.service.base.BaseService;
import com.haihun.sdk.vo.result.EquipmentResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 设备相关业务层
 *
 * @author kaiser·von·d
 * @version 2018/5/24
 */
@Slf4j
@Service
@Transactional(value = "masterTransactionManager", rollbackFor = {Exception.class, RuntimeException.class})
public class EquipmentService extends BaseService<Equipment> {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentChannelMapper equipmentChannelMapper;

    /**
     * 初始化设备
     *
     * @param equipment 设备信息
     * @return 初始化信息
     */
    public EquipmentResult init(Equipment equipment) {
        try {

            EquipmentResult result = new EquipmentResult();
            JSONObject retJson = new JSONObject();

            // 生成设置唯一标识符
            String duid = generatorDuid(equipment);

            EquipmentChannel ec = equipmentChannelMapper.findByEcId(equipment.getDuid());
            Equipment oldInfo;
            if (ec == null) {
                if (findByDuid(duid) == null) {
                    equipment.setDuid(duid);
                    // 保存设备信息
                    save(equipment);
                }
                ec = equipmentChannelMapper.findByDuidAndAppIdChannelId(duid, equipment.getAppKey(), equipment.getChannel());
                equipment.setDuid(duid);
                if (ec == null) {
                    ec = addEquipmentChannel(equipment);
                }
            } else {
                oldInfo = findByDuid(ec.getDuid());

                equipment.setDuid(duid);

                // 如果存在则更新
                if (oldInfo != null || (oldInfo = equipmentMapper.findByDuid(duid)) != null) {

                    // 将设备信息更新
                    BeanUtil.copyPropIgnoreNull(equipment, oldInfo);
                    // 更新设备
                    updateSelective(oldInfo);

                } else {
                    // 保存设备信息
                    save(equipment);
                }
            }

            //  记录设备日活跃度
            String ecId = ec.getEcId();
            Integer isActive = equipmentChannelMapper.findActive(ecId);
            if (isActive.equals(0)) {
                equipmentChannelMapper.addActiveStatus(ecId);
            }
            retJson.put("systemTime", String.valueOf(System.currentTimeMillis()));
            retJson.put("duid", ecId);
            result.setData(retJson.toJSONString());
            return result;
        } catch (Exception e) {
            log.error("sdk init failed ! detail : {} ", e.getMessage());
            throw new RuntimeException("sdk init failed ! detail : " + e.getMessage());
        }

    }

    public EquipmentChannel findEcByEcId(String ecId) {
        return equipmentChannelMapper.findByEcId(ecId);
    }


    private EquipmentChannel addEquipmentChannel(Equipment equipment) {
        EquipmentChannel channel = new EquipmentChannel();
        channel.setAppId(equipment.getAppKey());
        channel.setChannelId(equipment.getChannel());
        channel.setDuid(equipment.getDuid());
        channel.setCreateTime(new Date());

        String ecId = generatorEcId(channel);
        channel.setEcId(ecId);

        // 新增设备渠道信息
        equipmentChannelMapper.insertSelective(channel);
        return channel;
    }

    public Equipment findByDuid(String duid) {
        return equipmentMapper.findByDuid(duid);
    }

    /**
     * 根据设备信息生成16进制唯一设备标识符
     *
     * @param equipment 设备信息
     */
    public String generatorDuid(Equipment equipment) {
        String model = equipment.getModel();
        model = model == null ? "" : model;
        String imei = equipment.getImei();
        imei = imei == null ? "" : imei;
        String mac = equipment.getMac();
        mac = mac == null ? "" : mac;
        String serialno = equipment.getSerialNo();
        serialno = serialno == null ? "" : serialno;
        byte[] bytes = DigestUtils.sha256(model + ":" + imei + ":" + mac + ":" + serialno);
        return Hex.encodeHexString(bytes);
    }


    /**
     * 根据设备信息生成16进制唯一设备标识符
     *
     * @param ec 设备信息
     */
    public String generatorEcId(EquipmentChannel ec) {
        StringBuilder sb = new StringBuilder();
        sb.append(ec.getChannelId()).append(new DateTime(ec.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"));
        sb.append(ec.getAppId()).append(ec.getDuid());
        String ecId = DigestUtils.md5Hex(sb.toString());
        return ecId;
    }
}
