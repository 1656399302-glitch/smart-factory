package com.ruoyi.three.mapper;

import com.ruoyi.three.domain.DeviceSceneBinding;
import com.ruoyi.three.vo.SceneViewerDeviceVO;

import java.util.List;

/**
 * 点位绑定Mapper接口
 */
public interface DeviceSceneBindingMapper
{
    DeviceSceneBinding selectDeviceSceneBindingById(Long id);

    List<DeviceSceneBinding> selectDeviceSceneBindingList(DeviceSceneBinding deviceSceneBinding);

    int insertDeviceSceneBinding(DeviceSceneBinding deviceSceneBinding);

    int updateDeviceSceneBinding(DeviceSceneBinding deviceSceneBinding);

    int deleteDeviceSceneBindingById(Long id);

    int deleteDeviceSceneBindingByIds(Long[] ids);

    /**
     * Viewer 聚合查询：按 factoryId 获取发布态工厂模型下的设备点位列表
     */
    List<SceneViewerDeviceVO> selectViewerDeviceListByFactoryId(Long factoryId);
}


