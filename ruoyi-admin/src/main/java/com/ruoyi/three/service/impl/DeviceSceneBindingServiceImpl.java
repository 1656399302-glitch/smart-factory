package com.ruoyi.three.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.three.domain.DeviceSceneBinding;
import com.ruoyi.three.mapper.DeviceSceneBindingMapper;
import com.ruoyi.three.service.IDeviceSceneBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点位绑定Service业务层处理
 */
@Service
public class DeviceSceneBindingServiceImpl implements IDeviceSceneBindingService
{
    @Autowired
    private DeviceSceneBindingMapper deviceSceneBindingMapper;

    @Override
    public DeviceSceneBinding selectDeviceSceneBindingById(Long id)
    {
        return deviceSceneBindingMapper.selectDeviceSceneBindingById(id);
    }

    @Override
    public List<DeviceSceneBinding> selectDeviceSceneBindingList(DeviceSceneBinding deviceSceneBinding)
    {
        return deviceSceneBindingMapper.selectDeviceSceneBindingList(deviceSceneBinding);
    }

    @Override
    public int insertDeviceSceneBinding(DeviceSceneBinding deviceSceneBinding)
    {
        deviceSceneBinding.setCreateTime(DateUtils.getNowDate());
        return deviceSceneBindingMapper.insertDeviceSceneBinding(deviceSceneBinding);
    }

    @Override
    public int updateDeviceSceneBinding(DeviceSceneBinding deviceSceneBinding)
    {
        deviceSceneBinding.setUpdateTime(DateUtils.getNowDate());
        return deviceSceneBindingMapper.updateDeviceSceneBinding(deviceSceneBinding);
    }

    @Override
    public int deleteDeviceSceneBindingByIds(Long[] ids)
    {
        return deviceSceneBindingMapper.deleteDeviceSceneBindingByIds(ids);
    }

    @Override
    public int deleteDeviceSceneBindingById(Long id)
    {
        return deviceSceneBindingMapper.deleteDeviceSceneBindingById(id);
    }
}


