package com.ruoyi.three.vo;

/**
 * Viewer 聚合返回 - 工厂模型信息（published）
 */
public class SceneViewerFactoryModelVO
{
    private Long id;
    private Long factoryId;
    private String name;
    private String modelUrl;
    private String modelFormat;
    private String defaultCamera;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getFactoryId()
    {
        return factoryId;
    }

    public void setFactoryId(Long factoryId)
    {
        this.factoryId = factoryId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getModelUrl()
    {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl)
    {
        this.modelUrl = modelUrl;
    }

    public String getModelFormat()
    {
        return modelFormat;
    }

    public void setModelFormat(String modelFormat)
    {
        this.modelFormat = modelFormat;
    }

    public String getDefaultCamera()
    {
        return defaultCamera;
    }

    public void setDefaultCamera(String defaultCamera)
    {
        this.defaultCamera = defaultCamera;
    }
}


