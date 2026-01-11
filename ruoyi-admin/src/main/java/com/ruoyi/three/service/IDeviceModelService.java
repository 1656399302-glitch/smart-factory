package com.ruoyi.three.service;

import com.ruoyi.three.domain.DeviceModel;

import java.util.List;

/**
 * 设备模型Service接口
 */
public interface IDeviceModelService
{
    DeviceModel selectDeviceModelById(Long id);

    List<DeviceModel> selectDeviceModelList(DeviceModel deviceModel);

    int insertDeviceModel(DeviceModel deviceModel);

    int updateDeviceModel(DeviceModel deviceModel);

    int deleteDeviceModelByIds(Long[] ids);

    int deleteDeviceModelById(Long id);
}


