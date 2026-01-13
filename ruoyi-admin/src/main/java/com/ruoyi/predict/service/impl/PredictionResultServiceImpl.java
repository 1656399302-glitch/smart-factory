package com.ruoyi.predict.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.predict.mapper.PredictionResultMapper;
import com.ruoyi.predict.domain.PredictionResult;
import com.ruoyi.predict.service.IPredictionResultService;
import com.ruoyi.ar.domain.MaintenanceRecord;
import com.ruoyi.ar.mapper.MaintenanceRecordMapper;

/**
 * 预测结果Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@Service
public class PredictionResultServiceImpl implements IPredictionResultService {
    @Autowired
    private PredictionResultMapper predictionResultMapper;

    @Autowired
    private MaintenanceRecordMapper maintenanceRecordMapper;

    /**
     * 查询预测结果
     * 
     * @param id 预测结果主键
     * @return 预测结果
     */
    @Override
    public PredictionResult selectPredictionResultById(Long id) {
        return predictionResultMapper.selectPredictionResultById(id);
    }

    /**
     * 查询预测结果列表
     * 
     * @param predictionResult 预测结果
     * @return 预测结果
     */
    @Override
    public List<PredictionResult> selectPredictionResultList(PredictionResult predictionResult) {
        return predictionResultMapper.selectPredictionResultList(predictionResult);
    }

    /**
     * 新增预测结果
     * 
     * @param predictionResult 预测结果
     * @return 结果
     */
    @Override
    public int insertPredictionResult(PredictionResult predictionResult) {
        return predictionResultMapper.insertPredictionResult(predictionResult);
    }

    /**
     * 修改预测结果
     * 
     * @param predictionResult 预测结果
     * @return 结果
     */
    @Override
    public int updatePredictionResult(PredictionResult predictionResult) {
        return predictionResultMapper.updatePredictionResult(predictionResult);
    }

    /**
     * 批量删除预测结果
     * 
     * @param ids 需要删除的预测结果主键
     * @return 结果
     */
    @Override
    public int deletePredictionResultByIds(Long[] ids) {
        return predictionResultMapper.deletePredictionResultByIds(ids);
    }

    /**
     * 删除预测结果信息
     * 
     * @param id 预测结果主键
     * @return 结果
     */
    @Override
    public int deletePredictionResultById(Long id) {
        return predictionResultMapper.deletePredictionResultById(id);
    }

    /**
     * 确认预测结果（new -> acknowledged）
     * 
     * @param id       预测结果主键
     * @param username 处理人
     * @return 结果
     */
    @Override
    public int acknowledgePredictionResult(Long id, String username) {
        PredictionResult result = predictionResultMapper.selectPredictionResultById(id);
        if (result == null) {
            return 0;
        }
        // 只有new状态可以确认
        if (!"new".equals(result.getStatus())) {
            return 0;
        }
        PredictionResult update = new PredictionResult();
        update.setId(id);
        update.setStatus("acknowledged");
        update.setHandledBy(username);
        update.setHandledTime(new Date());
        update.setUpdateBy(username);
        return predictionResultMapper.updatePredictionResult(update);
    }

    /**
     * 忽略预测结果（new/acknowledged -> ignored）
     * 
     * @param id       预测结果主键
     * @param username 处理人
     * @return 结果
     */
    @Override
    public int ignorePredictionResult(Long id, String username) {
        PredictionResult result = predictionResultMapper.selectPredictionResultById(id);
        if (result == null) {
            return 0;
        }
        // new或acknowledged状态可以忽略
        if (!"new".equals(result.getStatus()) && !"acknowledged".equals(result.getStatus())) {
            return 0;
        }
        PredictionResult update = new PredictionResult();
        update.setId(id);
        update.setStatus("ignored");
        update.setHandledBy(username);
        update.setHandledTime(new Date());
        update.setUpdateBy(username);
        return predictionResultMapper.updatePredictionResult(update);
    }

    /**
     * 标记预测结果为已完成（acknowledged -> resolved）
     * 
     * @param id       预测结果主键
     * @param username 处理人
     * @return 结果
     */
    @Override
    public int resolvePredictionResult(Long id, String username) {
        PredictionResult result = predictionResultMapper.selectPredictionResultById(id);
        if (result == null) {
            return 0;
        }
        // 只有acknowledged状态可以标记完成
        if (!"acknowledged".equals(result.getStatus())) {
            return 0;
        }
        PredictionResult update = new PredictionResult();
        update.setId(id);
        update.setStatus("resolved");
        update.setHandledBy(username);
        update.setHandledTime(new Date());
        update.setUpdateBy(username);
        return predictionResultMapper.updatePredictionResult(update);
    }

    /**
     * 获取资产的维护记录列表
     * 
     * @param assetType 资产类型
     * @param assetId   资产ID
     * @return 维护记录列表
     */
    @Override
    public List<MaintenanceRecord> getMaintenanceRecordsByAsset(String assetType, Long assetId) {
        // maintenance_record.equipment_id 实际存储的是 device_number
        // 所以不管是 equipment 还是 device 类型，都用 assetId 查询
        if ("equipment".equals(assetType) || "device".equals(assetType)) {
            MaintenanceRecord query = new MaintenanceRecord();
            query.setEquipmentId(assetId);
            return maintenanceRecordMapper.selectMaintenanceRecordList(query);
        }
        return new ArrayList<>();
    }
}
