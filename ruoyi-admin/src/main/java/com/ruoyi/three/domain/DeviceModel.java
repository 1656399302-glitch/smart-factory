package com.ruoyi.three.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 设备模型对象 device_model
 */
public class DeviceModel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 分类（可选） */
    @Excel(name = "分类")
    private String category;

    /** 模型文件地址 */
    @Excel(name = "模型文件地址")
    private String modelUrl;

    /** 模型格式 */
    @Excel(name = "模型格式")
    private String modelFormat;

    /** 默认缩放（数值或 JSON 字符串） */
    private String defaultScale;

    /** 状态（enabled/disabled） */
    @Excel(name = "状态")
    private String status;

    /** 备注 */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public String getModelFormat() {
        return modelFormat;
    }

    public void setModelFormat(String modelFormat) {
        this.modelFormat = modelFormat;
    }

    public String getDefaultScale() {
        return defaultScale;
    }

    public void setDefaultScale(String defaultScale) {
        this.defaultScale = defaultScale;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("category", getCategory())
                .append("modelUrl", getModelUrl())
                .append("modelFormat", getModelFormat())
                .append("defaultScale", getDefaultScale())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
