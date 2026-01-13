package com.ruoyi.predict.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预测结果对象 prediction_result
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public class PredictionResult extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 工厂ID */
    @Excel(name = "工厂ID")
    private Long factoryId;

    /** 资产类型：equipment/device */
    @Excel(name = "资产类型")
    private String assetType;

    /** 资产ID */
    @Excel(name = "资产ID")
    private Long assetId;

    /** 预测生成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预测时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date predictionTime;

    /** 风险等级：low/medium/high */
    @Excel(name = "风险等级")
    private String riskLevel;

    /** 风险分数（0-100） */
    @Excel(name = "风险分数")
    private Integer riskScore;

    /** 原因摘要 */
    @Excel(name = "原因摘要")
    private String reasonSummary;

    /** 原因详情JSON（可解释） */
    private String reasonDetailJson;

    /** 建议维护动作 */
    @Excel(name = "建议")
    private String recommendation;

    /** 状态：new/acknowledged/ignored/resolved */
    @Excel(name = "状态")
    private String status;

    /** 处理人 */
    @Excel(name = "处理人")
    private String handledBy;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handledTime;

    /** 模型版本 */
    private String modelVersion;

    // ========== 非数据库字段，用于VO展示 ==========

    /** 资产名称（关联查询） */
    private String assetName;

    /** 工厂名称（关联查询） */
    private String factoryName;

    // ========== 查询条件字段 ==========

    /** 查询：预测时间开始 */
    private String beginPredictionTime;

    /** 查询：预测时间结束 */
    private String endPredictionTime;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setPredictionTime(Date predictionTime) {
        this.predictionTime = predictionTime;
    }

    public Date getPredictionTime() {
        return predictionTime;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setReasonSummary(String reasonSummary) {
        this.reasonSummary = reasonSummary;
    }

    public String getReasonSummary() {
        return reasonSummary;
    }

    public void setReasonDetailJson(String reasonDetailJson) {
        this.reasonDetailJson = reasonDetailJson;
    }

    public String getReasonDetailJson() {
        return reasonDetailJson;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRecommendation() {
        return recommendation;
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

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getBeginPredictionTime() {
        return beginPredictionTime;
    }

    public void setBeginPredictionTime(String beginPredictionTime) {
        this.beginPredictionTime = beginPredictionTime;
    }

    public String getEndPredictionTime() {
        return endPredictionTime;
    }

    public void setEndPredictionTime(String endPredictionTime) {
        this.endPredictionTime = endPredictionTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("factoryId", getFactoryId())
                .append("assetType", getAssetType())
                .append("assetId", getAssetId())
                .append("predictionTime", getPredictionTime())
                .append("riskLevel", getRiskLevel())
                .append("riskScore", getRiskScore())
                .append("reasonSummary", getReasonSummary())
                .append("reasonDetailJson", getReasonDetailJson())
                .append("recommendation", getRecommendation())
                .append("status", getStatus())
                .append("handledBy", getHandledBy())
                .append("handledTime", getHandledTime())
                .append("modelVersion", getModelVersion())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
