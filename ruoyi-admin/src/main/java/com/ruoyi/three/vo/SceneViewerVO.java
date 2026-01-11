package com.ruoyi.three.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Viewer 聚合返回
 */
public class SceneViewerVO
{
    /** 工厂发布态模型（可能为空） */
    private SceneViewerFactoryModelVO factoryModel;

    /** 设备点位列表（可能为空） */
    private List<SceneViewerDeviceVO> devices = new ArrayList<>();

    public SceneViewerFactoryModelVO getFactoryModel()
    {
        return factoryModel;
    }

    public void setFactoryModel(SceneViewerFactoryModelVO factoryModel)
    {
        this.factoryModel = factoryModel;
    }

    public List<SceneViewerDeviceVO> getDevices()
    {
        return devices;
    }

    public void setDevices(List<SceneViewerDeviceVO> devices)
    {
        this.devices = devices;
    }
}


