package com.ruoyi.predict.service;

import java.util.List;
import java.util.Map;

/**
 * 预测引擎Service接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public interface IPredictionEngineService {
    /**
     * 运行预测任务
     * 
     * @param assetType 资产类型（equipment/device）
     * @param factoryId 工厂ID（可选）
     * @param assetIds  资产ID列表（可选）
     * @param username  执行人
     * @return 执行结果（生成数量、跳过数量、耗时等）
     */
    public Map<String, Object> runPrediction(String assetType, Long factoryId, List<Long> assetIds, String username);
}
