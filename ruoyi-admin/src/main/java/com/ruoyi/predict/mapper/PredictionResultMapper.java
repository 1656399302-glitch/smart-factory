package com.ruoyi.predict.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.predict.domain.PredictionResult;
import org.apache.ibatis.annotations.Param;

/**
 * 预测结果Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public interface PredictionResultMapper {
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
     * 删除预测结果
     * 
     * @param id 预测结果主键
     * @return 结果
     */
    public int deletePredictionResultById(Long id);

    /**
     * 批量删除预测结果
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePredictionResultByIds(Long[] ids);

    /**
     * 检查当天是否已有预测记录
     * 
     * @param assetType 资产类型
     * @param assetId   资产ID
     * @param dateStr   日期字符串（yyyy-MM-dd）
     * @return 记录数
     */
    public int checkTodayPrediction(@Param("assetType") String assetType,
            @Param("assetId") Long assetId,
            @Param("dateStr") String dateStr);

    /**
     * 获取设备维护统计信息
     * 
     * @param equipmentId 设备ID
     * @return 统计信息Map
     */
    public Map<String, Object> getEquipmentMaintenanceStats(@Param("equipmentId") Long equipmentId);

    /**
     * 获取设备最近维护记录中的关键词命中情况
     * 
     * @param equipmentId 设备ID
     * @return 是否命中关键词
     */
    public int checkMaintenanceKeywords(@Param("equipmentId") Long equipmentId);
}
