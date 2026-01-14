package com.ruoyi.monitor.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报警记录对象 alarm_record
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public class AlarmRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 报警ID */
    private Long alarmId;

    /** 规则ID */
    @Excel(name = "规则ID")
    private Long ruleId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private Long deviceNumber;

    /** 传感器ID */
    @Excel(name = "传感器ID")
    private Long sensorId;

    /** 报警触发时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTime;

    /** 触发时的值 */
    @Excel(name = "触发值")
    private BigDecimal alarmValue;

    /** 严重程度（low/medium/high） */
    @Excel(name = "严重程度")
    private String severity;

    /** 状态（new/acknowledged/resolved） */
    @Excel(name = "状态")
    private String status;

    /** 处理人 */
    @Excel(name = "处理人")
    private String handledBy;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handledTime;

    /** 规则名称（关联查询） */
    private String ruleName;

    /** 传感器名称（关联查询） */
    private String sensorName;

    /** 传感器编码（关联查询） */
    private String sensorCode;

    /** 设备名称（关联查询） */
    private String deviceName;

    /** 单位（关联查询） */
    private String unit;

    /** 查询：开始时间 */
    private String beginAlarmTime;

    /** 查询：结束时间 */
    private String endAlarmTime;

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setDeviceNumber(Long deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Long getDeviceNumber() {
        return deviceNumber;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmValue(BigDecimal alarmValue) {
        this.alarmValue = alarmValue;
    }

    public BigDecimal getAlarmValue() {
        return alarmValue;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return severity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledTime(Date handledTime) {
        this.handledTime = handledTime;
    }

    public Date getHandledTime() {
        return handledTime;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBeginAlarmTime() {
        return beginAlarmTime;
    }

    public void setBeginAlarmTime(String beginAlarmTime) {
        this.beginAlarmTime = beginAlarmTime;
    }

    public String getEndAlarmTime() {
        return endAlarmTime;
    }

    public void setEndAlarmTime(String endAlarmTime) {
        this.endAlarmTime = endAlarmTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("alarmId", getAlarmId())
                .append("ruleId", getRuleId())
                .append("deviceNumber", getDeviceNumber())
                .append("sensorId", getSensorId())
                .append("alarmTime", getAlarmTime())
                .append("alarmValue", getAlarmValue())
                .append("severity", getSeverity())
                .append("status", getStatus())
                .append("handledBy", getHandledBy())
                .append("handledTime", getHandledTime())
                .append("remark", getRemark())
                .append("createTime", getCreateTime())
                .toString();
    }
}
