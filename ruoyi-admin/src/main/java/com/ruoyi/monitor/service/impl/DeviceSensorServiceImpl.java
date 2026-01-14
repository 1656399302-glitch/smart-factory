package com.ruoyi.monitor.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.monitor.mapper.DeviceSensorMapper;
import com.ruoyi.monitor.domain.DeviceSensor;
import com.ruoyi.monitor.service.IDeviceSensorService;

/**
 * 设备传感器Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@Service
public class DeviceSensorServiceImpl implements IDeviceSensorService {
    @Autowired
    private DeviceSensorMapper deviceSensorMapper;

    @Override
    public DeviceSensor selectDeviceSensorBySensorId(Long sensorId) {
        return deviceSensorMapper.selectDeviceSensorBySensorId(sensorId);
    }

    @Override
    public DeviceSensor selectByDeviceNumberAndSensorCode(Long deviceNumber, String sensorCode) {
        return deviceSensorMapper.selectByDeviceNumberAndSensorCode(deviceNumber, sensorCode);
    }

    @Override
    public List<DeviceSensor> selectDeviceSensorList(DeviceSensor deviceSensor) {
        return deviceSensorMapper.selectDeviceSensorList(deviceSensor);
    }

    @Override
    public List<DeviceSensor> selectByDeviceNumber(Long deviceNumber) {
        return deviceSensorMapper.selectByDeviceNumber(deviceNumber);
    }

    @Override
    public int insertDeviceSensor(DeviceSensor deviceSensor) {
        return deviceSensorMapper.insertDeviceSensor(deviceSensor);
    }

    @Override
    public int updateDeviceSensor(DeviceSensor deviceSensor) {
        return deviceSensorMapper.updateDeviceSensor(deviceSensor);
    }

    @Override
    public int deleteDeviceSensorBySensorIds(Long[] sensorIds) {
        return deviceSensorMapper.deleteDeviceSensorBySensorIds(sensorIds);
    }

    @Override
    public int deleteDeviceSensorBySensorId(Long sensorId) {
        return deviceSensorMapper.deleteDeviceSensorBySensorId(sensorId);
    }
}
