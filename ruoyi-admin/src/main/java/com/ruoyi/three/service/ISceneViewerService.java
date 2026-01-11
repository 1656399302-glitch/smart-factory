package com.ruoyi.three.service;

import com.ruoyi.three.vo.SceneViewerVO;

/**
 * Viewer 聚合Service接口
 */
public interface ISceneViewerService
{
    SceneViewerVO getViewerByFactoryId(Long factoryId);
}


