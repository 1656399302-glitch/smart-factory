package com.ruoyi.monitor.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 传感器数据对象 sensor_data
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public class SensorData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 传感器ID */
    @Excel(name = "传感器ID")
    private Long sensorId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private Long deviceNumber;

    /** 采集值 */
    @Excel(name = "采集值")
    private BigDecimal value;

    /** 采集时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "采集时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dataTime;

    /** 传感器名称（关联查询） */
    private String sensorName;

    /** 传感器编码（关联查询） */
    private String sensorCode;

    /** 单位（关联查询） */
    private String unit;

    /** 设备名称（关联查询） */
    private String deviceName;

    /** 查询：开始时间 */
    private String beginDataTime;

    /** 查询：结束时间 */
    private String endDataTime;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

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

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorCode() {
        return sensorCode;
    }

    public void setSensorCode(String sensorCode) {
        this.sensorCode = sensorCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBeginDataTime() {
        return beginDataTime;
    }

    public void setBeginDataTime(String beginDataTime) {
        this.beginDataTime = beginDataTime;
    }

    public String getEndDataTime() {
        return endDataTime;
    }

    public void setEndDataTime(String endDataTime) {
        this.endDataTime = endDataTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sensorId", getSensorId())
                .append("deviceNumber", getDeviceNumber())
                .append("value", getValue())
                .append("dataTime", getDataTime())
                .append("createTime", getCreateTime())
                .toString();
    }
}
