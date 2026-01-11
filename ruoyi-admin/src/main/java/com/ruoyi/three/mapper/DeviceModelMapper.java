package com.ruoyi.three.mapper;

import com.ruoyi.three.domain.DeviceModel;

import java.util.List;

/**
 * 设备模型Mapper接口
 */
public interface DeviceModelMapper
{
    DeviceModel selectDeviceModelById(Long id);

    List<DeviceModel> selectDeviceModelList(DeviceModel deviceModel);

    int insertDeviceModel(DeviceModel deviceModel);

    int updateDeviceModel(DeviceModel deviceModel);

    int deleteDeviceModelById(Long id);

    int deleteDeviceModelByIds(Long[] ids);
}


