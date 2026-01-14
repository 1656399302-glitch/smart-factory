package com.ruoyi.monitor.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.monitor.domain.SensorData;

/**
 * 传感器数据Service接口
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public interface ISensorDataService {
    /**
     * 查询传感器数据
     */
    public SensorData selectSensorDataById(Long id);

    /**
     * 查询传感器数据列表
     */
    public List<SensorData> selectSensorDataList(SensorData sensorData);

    /**
     * 查询指定设备各传感器的最新数据
     */
    public List<SensorData> selectLatestDataByDeviceNumber(Long deviceNumber);

    /**
     * 采集数据上报（核心方法）
     * 
     * @param deviceNumber 设备编号
     * @param sensorCode   传感器编码
     * @param value        采集值
     * @param dataTime     采集时间（可选，不传则使用服务器时间）
     * @return 处理结果
     */
    public Map<String, Object> collectData(Long deviceNumber, String sensorCode,
            java.math.BigDecimal value, java.util.Date dataTime);

    /**
     * 新增传感器数据
     */
    public int insertSensorData(SensorData sensorData);

    /**
     * 删除传感器数据
     */
    public int deleteSensorDataById(Long id);

    /**
     * 批量删除传感器数据
     */
    public int deleteSensorDataByIds(Long[] ids);
}
