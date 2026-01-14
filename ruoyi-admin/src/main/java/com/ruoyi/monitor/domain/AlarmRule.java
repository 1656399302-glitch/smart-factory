package com.ruoyi.monitor.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报警规则对象 alarm_rule
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public class AlarmRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 传感器ID */
    @Excel(name = "传感器ID")
    private Long sensorId;

    /** 比较类型（gt/ge/lt/le/between） */
    @Excel(name = "比较类型")
    private String compareType;

    /** 阈值下限 */
    @Excel(name = "阈值下限")
    private BigDecimal thresholdMin;

    /** 阈值上限 */
    @Excel(name = "阈值上限")
    private BigDecimal thresholdMax;

    /** 严重程度（low/medium/high） */
    @Excel(name = "严重程度")
    private String severity;

    /** 是否启用（0停用 1启用） */
    @Excel(name = "是否启用", readConverterExp = "0=停用,1=启用")
    private String enabled;

    /** 规则描述 */
    @Excel(name = "规则描述")
    private String description;

    /** 传感器名称（关联查询） */
    private String sensorName;

    /** 传感器编码（关联查询） */
    private String sensorCode;

    /** 设备编号（关联查询） */
    private Long deviceNumber;

    /** 设备名称（关联查询） */
    private String deviceName;

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setCompareType(String compareType) {
        this.compareType = compareType;
    }

    public String getCompareType() {
        return compareType;
    }

    public void setThresholdMin(BigDecimal thresholdMin) {
        this.thresholdMin = thresholdMin;
    }

    public BigDecimal getThresholdMin() {
        return thresholdMin;
    }

    public void setThresholdMax(BigDecimal thresholdMax) {
        this.thresholdMax = thresholdMax;
    }

    public BigDecimal getThresholdMax() {
        return thresholdMax;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return severity;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
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

    public Long getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(Long deviceNumber) {
        this.deviceNumber = deviceNumber;
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
                .append("ruleId", getRuleId())
                .append("ruleName", getRuleName())
                .append("sensorId", getSensorId())
                .append("compareType", getCompareType())
                .append("thresholdMin", getThresholdMin())
                .append("thresholdMax", getThresholdMax())
                .append("severity", getSeverity())
                .append("enabled", getEnabled())
                .append("description", getDescription())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
