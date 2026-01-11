package com.ruoyi.three.mapper;

import com.ruoyi.three.domain.FactoryModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工厂模型Mapper接口
 */
public interface FactoryModelMapper
{
    FactoryModel selectFactoryModelById(Long id);

    List<FactoryModel> selectFactoryModelList(FactoryModel factoryModel);

    FactoryModel selectPublishedFactoryModelByFactoryId(Long factoryId);

    List<FactoryModel> selectFactoryModelByFactoryId(Long factoryId);

    int insertFactoryModel(FactoryModel factoryModel);

    int updateFactoryModel(FactoryModel factoryModel);

    int deleteFactoryModelById(Long id);

    int deleteFactoryModelByIds(Long[] ids);

    int updateFactoryModelStatusByFactoryId(@Param("factoryId") Long factoryId,
                                            @Param("status") String status,
                                            @Param("excludeId") Long excludeId);

    int updateFactoryModelStatusById(@Param("id") Long id, @Param("status") String status);
}


