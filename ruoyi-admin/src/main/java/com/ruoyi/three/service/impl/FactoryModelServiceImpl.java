package com.ruoyi.three.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.three.domain.FactoryModel;
import com.ruoyi.three.mapper.FactoryModelMapper;
import com.ruoyi.three.service.IFactoryModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工厂模型Service业务层处理
 */
@Service
public class FactoryModelServiceImpl implements IFactoryModelService
{
    @Autowired
    private FactoryModelMapper factoryModelMapper;

    @Override
    public FactoryModel selectFactoryModelById(Long id)
    {
        return factoryModelMapper.selectFactoryModelById(id);
    }

    @Override
    public List<FactoryModel> selectFactoryModelList(FactoryModel factoryModel)
    {
        return factoryModelMapper.selectFactoryModelList(factoryModel);
    }

    @Override
    public List<FactoryModel> selectFactoryModelByFactoryId(Long factoryId)
    {
        return factoryModelMapper.selectFactoryModelByFactoryId(factoryId);
    }

    @Override
    public FactoryModel selectPublishedFactoryModelByFactoryId(Long factoryId)
    {
        return factoryModelMapper.selectPublishedFactoryModelByFactoryId(factoryId);
    }

    @Override
    public int insertFactoryModel(FactoryModel factoryModel)
    {
        factoryModel.setCreateTime(DateUtils.getNowDate());
        return factoryModelMapper.insertFactoryModel(factoryModel);
    }

    @Override
    public int updateFactoryModel(FactoryModel factoryModel)
    {
        factoryModel.setUpdateTime(DateUtils.getNowDate());
        return factoryModelMapper.updateFactoryModel(factoryModel);
    }

    @Override
    public int deleteFactoryModelByIds(Long[] ids)
    {
        return factoryModelMapper.deleteFactoryModelByIds(ids);
    }

    @Override
    public int deleteFactoryModelById(Long id)
    {
        return factoryModelMapper.deleteFactoryModelById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishFactoryModel(Long id)
    {
        FactoryModel model = factoryModelMapper.selectFactoryModelById(id);
        if (model == null)
        {
            throw new ServiceException("工厂模型不存在");
        }
        if (model.getFactoryId() == null)
        {
            throw new ServiceException("工厂ID不能为空");
        }

        // 将同 factory 的其它 published 降级为 draft
        factoryModelMapper.updateFactoryModelStatusByFactoryId(model.getFactoryId(), "draft", id);
        // 将当前模型置为 published
        factoryModelMapper.updateFactoryModelStatusById(id, "published");
    }
}


