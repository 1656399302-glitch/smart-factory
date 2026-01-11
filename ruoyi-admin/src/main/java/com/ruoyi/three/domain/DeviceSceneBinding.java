package com.ruoyi.three.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 设备点位绑定对象 device_scene_binding
 */
public class DeviceSceneBinding extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联 factory_model.id */
    @Excel(name = "工厂模型ID")
    private Long factoryModelId;

    /** 关联 device.device_number（只读表） */
    @Excel(name = "设备编号")
    private Long deviceNumber;

    /** 关联 device_model.id */
    @Excel(name = "设备模型ID")
    private Long deviceModelId;

    /** 位置 */
    private BigDecimal positionX;
    private BigDecimal positionY;
    private BigDecimal positionZ;

    /** 旋转 */
    private BigDecimal rotationX;
    private BigDecimal rotationY;
    private BigDecimal rotationZ;

    /** 缩放 */
    private BigDecimal scaleX;
    private BigDecimal scaleY;
    private BigDecimal scaleZ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFactoryModelId() {
        return factoryModelId;
    }

    public void setFactoryModelId(Long factoryModelId) {
        this.factoryModelId = factoryModelId;
    }

    public Long getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(Long deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Long getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(Long deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public BigDecimal getPositionX() {
        return positionX;
    }

    public void setPositionX(BigDecimal positionX) {
        this.positionX = positionX;
    }

    public BigDecimal getPositionY() {
        return positionY;
    }

    public void setPositionY(BigDecimal positionY) {
        this.positionY = positionY;
    }

    public BigDecimal getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(BigDecimal positionZ) {
        this.positionZ = positionZ;
    }

    public BigDecimal getRotationX() {
        return rotationX;
    }

    public void setRotationX(BigDecimal rotationX) {
        this.rotationX = rotationX;
    }

    public BigDecimal getRotationY() {
        return rotationY;
    }

    public void setRotationY(BigDecimal rotationY) {
        this.rotationY = rotationY;
    }

    public BigDecimal getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(BigDecimal rotationZ) {
        this.rotationZ = rotationZ;
    }

    public BigDecimal getScaleX() {
        return scaleX;
    }

    public void setScaleX(BigDecimal scaleX) {
        this.scaleX = scaleX;
    }

    public BigDecimal getScaleY() {
        return scaleY;
    }

    public void setScaleY(BigDecimal scaleY) {
        this.scaleY = scaleY;
    }

    public BigDecimal getScaleZ() {
        return scaleZ;
    }

    public void setScaleZ(BigDecimal scaleZ) {
        this.scaleZ = scaleZ;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("factoryModelId", getFactoryModelId())
                .append("deviceNumber", getDeviceNumber())
                .append("deviceModelId", getDeviceModelId())
                .append("positionX", getPositionX())
                .append("positionY", getPositionY())
                .append("positionZ", getPositionZ())
                .append("rotationX", getRotationX())
                .append("rotationY", getRotationY())
                .append("rotationZ", getRotationZ())
                .append("scaleX", getScaleX())
                .append("scaleY", getScaleY())
                .append("scaleZ", getScaleZ())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
