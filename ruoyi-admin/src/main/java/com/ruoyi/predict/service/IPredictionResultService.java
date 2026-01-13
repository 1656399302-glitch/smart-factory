package com.ruoyi.predict.service;

import java.util.List;
import com.ruoyi.predict.domain.PredictionResult;
import com.ruoyi.ar.domain.MaintenanceRecord;

/**
 * 预测结果Service接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public interface IPredictionResultService {
    /**
     * 查询预测结果
     * 
     * @param id 预测结果主键
     * @return 预测结果
     */
    public PredictionResult selectPredictionResultById(Long id);

    /**
     * 查询预测结果列表
     * 
     * @param predictionResult 预测结果
     * @return 预测结果集合
     */
    public List<PredictionResult> selectPredictionResultList(PredictionResult predictionResult);

    /**
     * 新增预测结果
     * 
     * @param predictionResult 预测结果
     * @return 结果
     */
    public int insertPredictionResult(PredictionResult predictionResult);

    /**
     * 修改预测结果
     * 
     * @param predictionResult 预测结果
     * @return 结果
     */
    public int updatePredictionResult(PredictionResult predictionResult);

    /**
     * 批量删除预测结果
     * 
     * @param ids 需要删除的预测结果主键集合
     * @return 结果
     */
    public int deletePredictionResultByIds(Long[] ids);

    /**
     * 删除预测结果信息
     * 
     * @param id 预测结果主键
     * @return 结果
     */
    public int deletePredictionResultById(Long id);

    /**
     * 确认预测结果
     * 
     * @param id       预测结果主键
     * @param username 处理人
     * @return 结果
     */
    public int acknowledgePredictionResult(Long id, String username);

    /**
     * 忽略预测结果
     * 
     * @param id       预测结果主键
     * @param username 处理人
     * @return 结果
     */
    public int ignorePredictionResult(Long id, String username);

    /**
     * 标记预测结果为已完成
     * 
     * @param id       预测结果主键
     * @param username 处理人
     * @return 结果
     */
    public int resolvePredictionResult(Long id, String username);

    /**
     * 获取资产的维护记录列表
     * 
     * @param assetType 资产类型
     * @param assetId   资产ID
     * @return 维护记录列表
     */
    public List<MaintenanceRecord> getMaintenanceRecordsByAsset(String assetType, Long assetId);
}
