package com.ruoyi.predict.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 预测模块设备Mapper接口（只读查询）
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public interface PredictEquipmentMapper {
    /**
     * 查询equipment设备列表（用于预测）
     * 
     * @param factoryId    工厂ID（可选）
     * @param equipmentIds 设备ID列表（可选）
     * @return 设备信息列表
     */
    public List<Map<String, Object>> selectEquipmentForPrediction(@Param("factoryId") Long factoryId,
            @Param("equipmentIds") List<Long> equipmentIds);

    /**
     * 查询device设备列表（用于预测）
     * 
     * @param factoryId 工厂ID（可选）
     * @param deviceIds 设备ID列表（可选）
     * @return 设备信息列表
     */
    public List<Map<String, Object>> selectDeviceForPrediction(@Param("factoryId") Long factoryId,
            @Param("deviceIds") List<Long> deviceIds);

    /**
     * 根据ID查询equipment设备
     * 
     * @param equipmentId 设备ID
     * @return 设备信息
     */
    public Map<String, Object> selectEquipmentById(@Param("equipmentId") Long equipmentId);
}
