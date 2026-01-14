package com.ruoyi.monitor.mapper;

import java.util.List;
import com.ruoyi.monitor.domain.DeviceSensor;

/**
 * 设备传感器Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public interface DeviceSensorMapper {
    /**
     * 查询设备传感器
     */
    public DeviceSensor selectDeviceSensorBySensorId(Long sensorId);

    /**
     * 根据设备编号和传感器编码查询
     */
    public DeviceSensor selectByDeviceNumberAndSensorCode(Long deviceNumber, String sensorCode);

    /**
     * 查询设备传感器列表
     */
    public List<DeviceSensor> selectDeviceSensorList(DeviceSensor deviceSensor);

    /**
     * 查询指定设备的所有传感器
     */
    public List<DeviceSensor> selectByDeviceNumber(Long deviceNumber);

    /**
     * 新增设备传感器
     */
    public int insertDeviceSensor(DeviceSensor deviceSensor);

    /**
     * 修改设备传感器
     */
    public int updateDeviceSensor(DeviceSensor deviceSensor);

    /**
     * 删除设备传感器
     */
    public int deleteDeviceSensorBySensorId(Long sensorId);

    /**
     * 批量删除设备传感器
     */
    public int deleteDeviceSensorBySensorIds(Long[] sensorIds);
}
