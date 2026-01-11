package com.ruoyi.three.service.impl;

import com.ruoyi.three.domain.FactoryModel;
import com.ruoyi.three.mapper.DeviceSceneBindingMapper;
import com.ruoyi.three.service.IFactoryModelService;
import com.ruoyi.three.service.ISceneViewerService;
import com.ruoyi.three.vo.SceneViewerDeviceVO;
import com.ruoyi.three.vo.SceneViewerFactoryModelVO;
import com.ruoyi.three.vo.SceneViewerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Viewer 聚合Service业务层处理
 */
@Service
public class SceneViewerServiceImpl implements ISceneViewerService
{
    @Autowired
    private IFactoryModelService factoryModelService;

    @Autowired
    private DeviceSceneBindingMapper deviceSceneBindingMapper;

    @Override
    public SceneViewerVO getViewerByFactoryId(Long factoryId)
    {
        SceneViewerVO vo = new SceneViewerVO();

        FactoryModel published = factoryModelService.selectPublishedFactoryModelByFactoryId(factoryId);
        if (published == null)
        {
            vo.setFactoryModel(null);
            vo.setDevices(Collections.emptyList());
            return vo;
        }

        SceneViewerFactoryModelVO fm = new SceneViewerFactoryModelVO();
        fm.setId(published.getId());
        fm.setFactoryId(published.getFactoryId());
        fm.setName(published.getName());
        fm.setModelUrl(published.getModelUrl());
        fm.setModelFormat(published.getModelFormat());
        fm.setDefaultCamera(published.getDefaultCamera());
        vo.setFactoryModel(fm);

        List<SceneViewerDeviceVO> devices = deviceSceneBindingMapper.selectViewerDeviceListByFactoryId(factoryId);
        for (SceneViewerDeviceVO d : devices)
        {
            d.setStatusColor(mapStatusToColor(d.getStatus()));
        }
        vo.setDevices(devices);
        return vo;
    }

    private String mapStatusToColor(String status)
    {
        if (status == null)
        {
            return "#9e9e9e";
        }
        switch (status)
        {
            case "running":
                return "#00c853";
            case "stopped":
                return "#9e9e9e";
            case "alarm":
                return "#f44336";
            case "offline":
                return "#616161";
            default:
                return "#9e9e9e";
        }
    }
}


