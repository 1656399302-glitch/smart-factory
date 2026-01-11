package com.ruoyi.three.service;

import com.ruoyi.three.domain.FactoryModel;

import java.util.List;

/**
 * 工厂模型Service接口
 */
public interface IFactoryModelService
{
    FactoryModel selectFactoryModelById(Long id);

    List<FactoryModel> selectFactoryModelList(FactoryModel factoryModel);

    List<FactoryModel> selectFactoryModelByFactoryId(Long factoryId);

    FactoryModel selectPublishedFactoryModelByFactoryId(Long factoryId);

    int insertFactoryModel(FactoryModel factoryModel);

    int updateFactoryModel(FactoryModel factoryModel);

    int deleteFactoryModelByIds(Long[] ids);

    int deleteFactoryModelById(Long id);

    /**
     * 发布工厂模型：同 factory 仅允许一个 published（事务内保证）
     */
    void publishFactoryModel(Long id);
}


