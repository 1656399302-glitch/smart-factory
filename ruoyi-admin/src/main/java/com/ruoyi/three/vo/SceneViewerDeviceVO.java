package com.ruoyi.three.vo;

import java.math.BigDecimal;

/**
 * Viewer 聚合返回 - 设备点位信息（绑定 + device 基础信息 + 设备模型信息）
 */
public class SceneViewerDeviceVO
{
    private Long deviceNumber;
    private String name;
    private String status;
    /** running/stopped/alarm/offline -> 颜色（后端固定规则） */
    private String statusColor;

    /** 点位绑定ID（为空表示未绑定） */
    private Long bindingId;

    private Long deviceModelId;
    private String deviceModelUrl;
    private String deviceModelFormat;

    private BigDecimal positionX;
    private BigDecimal positionY;
    private BigDecimal positionZ;
    private BigDecimal rotationX;
    private BigDecimal rotationY;
    private BigDecimal rotationZ;
    private BigDecimal scaleX;
    private BigDecimal scaleY;
    private BigDecimal scaleZ;

    public Long getDeviceNumber()
    {
        return deviceNumber;
    }

    public void setDeviceNumber(Long deviceNumber)
    {
        this.deviceNumber = deviceNumber;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusColor()
    {
        return statusColor;
    }

    public void setStatusColor(String statusColor)
    {
        this.statusColor = statusColor;
    }

    public Long getBindingId()
    {
        return bindingId;
    }

    public void setBindingId(Long bindingId)
    {
        this.bindingId = bindingId;
    }

    public Long getDeviceModelId()
    {
        return deviceModelId;
    }

    public void setDeviceModelId(Long deviceModelId)
    {
        this.deviceModelId = deviceModelId;
    }

    public String getDeviceModelUrl()
    {
        return deviceModelUrl;
    }

    public void setDeviceModelUrl(String deviceModelUrl)
    {
        this.deviceModelUrl = deviceModelUrl;
    }

    public String getDeviceModelFormat()
    {
        return deviceModelFormat;
    }

    public void setDeviceModelFormat(String deviceModelFormat)
    {
        this.deviceModelFormat = deviceModelFormat;
    }

    public BigDecimal getPositionX()
    {
        return positionX;
    }

    public void setPositionX(BigDecimal positionX)
    {
        this.positionX = positionX;
    }

    public BigDecimal getPositionY()
    {
        return positionY;
    }

    public void setPositionY(BigDecimal positionY)
    {
        this.positionY = positionY;
    }

    public BigDecimal getPositionZ()
    {
        return positionZ;
    }

    public void setPositionZ(BigDecimal positionZ)
    {
        this.positionZ = positionZ;
    }

    public BigDecimal getRotationX()
    {
        return rotationX;
    }

    public void setRotationX(BigDecimal rotationX)
    {
        this.rotationX = rotationX;
    }

    public BigDecimal getRotationY()
    {
        return rotationY;
    }

    public void setRotationY(BigDecimal rotationY)
    {
        this.rotationY = rotationY;
    }

    public BigDecimal getRotationZ()
    {
        return rotationZ;
    }

    public void setRotationZ(BigDecimal rotationZ)
    {
        this.rotationZ = rotationZ;
    }

    public BigDecimal getScaleX()
    {
        return scaleX;
    }

    public void setScaleX(BigDecimal scaleX)
    {
        this.scaleX = scaleX;
    }

    public BigDecimal getScaleY()
    {
        return scaleY;
    }

    public void setScaleY(BigDecimal scaleY)
    {
        this.scaleY = scaleY;
    }

    public BigDecimal getScaleZ()
    {
        return scaleZ;
    }

    public void setScaleZ(BigDecimal scaleZ)
    {
        this.scaleZ = scaleZ;
    }
}


