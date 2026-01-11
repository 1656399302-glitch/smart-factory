package com.ruoyi.three.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工厂模型对象 factory_model
 *
 * 说明：M1 三维可视化模块，仅新增自身表结构；不修改现有 factory/device 表。
 */
public class FactoryModel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联 factory.factory_id */
    @Excel(name = "工厂ID")
    private Long factoryId;

    /** 模型名称 */
    @Excel(name = "模型名称")
    private String name;

    /** 模型文件地址（glb/gltf） */
    @Excel(name = "模型文件地址")
    private String modelUrl;

    /** 模型格式（glb/gltf） */
    @Excel(name = "模型格式")
    private String modelFormat;

    /** 状态（draft/published） */
    @Excel(name = "状态")
    private String status;

    /** 默认相机（JSON 字符串） */
    private String defaultCamera;

    /** 备注 */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDefaultCamera() {
        return defaultCamera;
    }

    public void setDefaultCamera(String defaultCamera) {
        this.defaultCamera = defaultCamera;
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
                .append("factoryId", getFactoryId())
                .append("name", getName())
                .append("modelUrl", getModelUrl())
                .append("modelFormat", getModelFormat())
                .append("status", getStatus())
                .append("defaultCamera", getDefaultCamera())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
