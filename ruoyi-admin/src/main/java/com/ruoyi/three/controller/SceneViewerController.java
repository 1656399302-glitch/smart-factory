package com.ruoyi.three.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.three.service.ISceneViewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 三维总览 Viewer 聚合接口（M1）
 */
@RestController
@RequestMapping("/scene")
public class SceneViewerController extends BaseController
{
    @Autowired
    private ISceneViewerService sceneViewerService;

    /**
     * 获取指定工厂的 Viewer 数据（发布态工厂模型 + 设备点位 + 状态）
     */
    @PreAuthorize("@ss.hasPermi('three:viewer:view')")
    @GetMapping("/viewer/{factoryId}")
    public AjaxResult viewer(@PathVariable("factoryId") Long factoryId)
    {
        return AjaxResult.success(sceneViewerService.getViewerByFactoryId(factoryId));
    }
}


