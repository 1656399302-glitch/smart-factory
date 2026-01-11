package com.ruoyi.three.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.three.domain.DeviceModel;
import com.ruoyi.three.mapper.DeviceModelMapper;
import com.ruoyi.three.service.IDeviceModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备模型Service业务层处理
 */
@Service
public class DeviceModelServiceImpl implements IDeviceModelService
{
    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Override
    public DeviceModel selectDeviceModelById(Long id)
    {
        return deviceModelMapper.selectDeviceModelById(id);
    }

    @Override
    public List<DeviceModel> selectDeviceModelList(DeviceModel deviceModel)
    {
        return deviceModelMapper.selectDeviceModelList(deviceModel);
    }

    @Override
    public int insertDeviceModel(DeviceModel deviceModel)
    {
        deviceModel.setCreateTime(DateUtils.getNowDate());
        return deviceModelMapper.insertDeviceModel(deviceModel);
    }

    @Override
    public int updateDeviceModel(DeviceModel deviceModel)
    {
        deviceModel.setUpdateTime(DateUtils.getNowDate());
        return deviceModelMapper.updateDeviceModel(deviceModel);
    }

    @Override
    public int deleteDeviceModelByIds(Long[] ids)
    {
        return deviceModelMapper.deleteDeviceModelByIds(ids);
    }

    @Override
    public int deleteDeviceModelById(Long id)
    {
        return deviceModelMapper.deleteDeviceModelById(id);
    }
}


