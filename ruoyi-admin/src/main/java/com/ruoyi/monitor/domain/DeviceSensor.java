package com.ruoyi.monitor.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备传感器对象 device_sensor
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public class DeviceSensor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 传感器ID */
    private Long sensorId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private Long deviceNumber;

    /** 传感器编码 */
    @Excel(name = "传感器编码")
    private String sensorCode;

    /** 传感器名称 */
    @Excel(name = "传感器名称")
    private String sensorName;

    /** 传感器类型 */
    @Excel(name = "传感器类型")
    private String sensorType;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 采集周期(秒) */
    @Excel(name = "采集周期(秒)")
    private Integer collectInterval;

    /** 状态（0启用 1停用） */
    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    private String status;

    /** 设备名称（关联查询） */
    private String deviceName;

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setDeviceNumber(Long deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Long getDeviceNumber() {
        return deviceNumber;
    }

    public void setSensorCode(String sensorCode) {
        this.sensorCode = sensorCode;
    }

    public String getSensorCode() {
        return sensorCode;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setCollectInterval(Integer collectInterval) {
        this.collectInterval = collectInterval;
    }

    public Integer getCollectInterval() {
        return collectInterval;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("sensorId", getSensorId())
                .append("deviceNumber", getDeviceNumber())
                .append("sensorCode", getSensorCode())
                .append("sensorName", getSensorName())
                .append("sensorType", getSensorType())
                .append("unit", getUnit())
                .append("collectInterval", getCollectInterval())
                .append("status", getStatus())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
