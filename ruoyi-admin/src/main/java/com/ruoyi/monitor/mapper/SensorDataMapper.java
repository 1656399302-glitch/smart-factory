package com.ruoyi.monitor.mapper;

import java.util.List;
import com.ruoyi.monitor.domain.SensorData;

/**
 * 传感器数据Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public interface SensorDataMapper {
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
     * 查询指定传感器的最新数据
     */
    public SensorData selectLatestDataBySensorId(Long sensorId);

    /**
     * 新增传感器数据
     */
    public int insertSensorData(SensorData sensorData);

    /**
     * 批量新增传感器数据
     */
    public int batchInsertSensorData(List<SensorData> list);

    /**
     * 删除传感器数据
     */
    public int deleteSensorDataById(Long id);

    /**
     * 批量删除传感器数据
     */
    public int deleteSensorDataByIds(Long[] ids);
}
