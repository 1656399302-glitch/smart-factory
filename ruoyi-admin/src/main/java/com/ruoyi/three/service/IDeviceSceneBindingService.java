package com.ruoyi.three.service;

import com.ruoyi.three.domain.DeviceSceneBinding;

import java.util.List;

/**
 * 点位绑定Service接口
 */
public interface IDeviceSceneBindingService
{
    DeviceSceneBinding selectDeviceSceneBindingById(Long id);

    List<DeviceSceneBinding> selectDeviceSceneBindingList(DeviceSceneBinding deviceSceneBinding);

    int insertDeviceSceneBinding(DeviceSceneBinding deviceSceneBinding);

    int updateDeviceSceneBinding(DeviceSceneBinding deviceSceneBinding);

    int deleteDeviceSceneBindingByIds(Long[] ids);

    int deleteDeviceSceneBindingById(Long id);
}


