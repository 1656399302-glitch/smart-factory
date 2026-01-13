package com.ruoyi.predict.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.predict.domain.PredictionResult;
import com.ruoyi.predict.mapper.PredictionResultMapper;
import com.ruoyi.predict.mapper.PredictEquipmentMapper;
import com.ruoyi.predict.service.IPredictionEngineService;

/**
 * 预测引擎Service业务层处理（P0规则版本）
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@Service
public class PredictionEngineServiceImpl implements IPredictionEngineService {
    private static final Logger log = LoggerFactory.getLogger(PredictionEngineServiceImpl.class);

    private static final String MODEL_VERSION = "RULE_P0_v1";

    @Autowired
    private PredictionResultMapper predictionResultMapper;

    @Autowired
    private PredictEquipmentMapper predictEquipmentMapper;

    /**
     * 运行预测任务
     * 
     * @param assetType 资产类型（equipment/device）
     * @param factoryId 工厂ID（可选）
     * @param assetIds  资产ID列表（可选）
     * @param username  执行人
     * @return 执行结果
     */
    @Override
    public Map<String, Object> runPrediction(String assetType, Long factoryId, List<Long> assetIds, String username) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        int generatedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();

        // 支持equipment和device类型
        if (!"equipment".equals(assetType) && !"device".equals(assetType)) {
            result.put("success", false);
            result.put("message", "资产类型必须是equipment或device");
            result.put("generatedCount", 0);
            result.put("skippedCount", 0);
            result.put("errorCount", 1);
            result.put("duration", System.currentTimeMillis() - startTime);
            return result;
        }

        try {
            // 查询目标设备列表（根据assetType选择不同的表）
            List<Map<String, Object>> deviceList;
            if ("device".equals(assetType)) {
                deviceList = predictEquipmentMapper.selectDeviceForPrediction(factoryId, assetIds);
            } else {
                deviceList = predictEquipmentMapper.selectEquipmentForPrediction(factoryId, assetIds);
            }

            if (deviceList == null || deviceList.isEmpty()) {
                result.put("success", true);
                result.put("message", "未找到符合条件的设备");
                result.put("generatedCount", 0);
                result.put("skippedCount", 0);
                result.put("errorCount", 0);
                result.put("duration", System.currentTimeMillis() - startTime);
                return result;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            for (Map<String, Object> device : deviceList) {
                // 根据assetType获取不同的ID字段
                Long deviceId;
                Long devFactoryId;
                if ("device".equals(assetType)) {
                    deviceId = getLongValue(device.get("device_number"));
                    devFactoryId = getLongValue(device.get("factory_number"));
                } else {
                    deviceId = getLongValue(device.get("equipment_id"));
                    devFactoryId = getLongValue(device.get("factory_id"));
                }

                if (deviceId == null) {
                    errorCount++;
                    errors.add("设备ID为空");
                    continue;
                }

                try {
                    // 检查当天是否已有预测记录
                    int existCount = predictionResultMapper.checkTodayPrediction(assetType, deviceId, today);
                    if (existCount > 0) {
                        skippedCount++;
                        continue;
                    }

                    // 计算风险分数（maintenance_record.equipment_id 实际存储的是device_number）
                    PredictionResult prediction = calculateRisk(assetType, deviceId, devFactoryId, username);

                    if (prediction != null) {
                        predictionResultMapper.insertPredictionResult(prediction);
                        generatedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    log.error("预测设备{}时发生错误: {}", deviceId, e.getMessage());
                    errorCount++;
                    errors.add("设备" + deviceId + ": " + e.getMessage());
                }
            }

            result.put("success", true);
            result.put("message", "预测任务完成");

        } catch (Exception e) {
            log.error("预测任务执行失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "预测任务执行失败: " + e.getMessage());
            errorCount++;
        }

        result.put("generatedCount", generatedCount);
        result.put("skippedCount", skippedCount);
        result.put("errorCount", errorCount);
        result.put("duration", System.currentTimeMillis() - startTime);
        if (!errors.isEmpty()) {
            result.put("errors", errors);
        }

        return result;
    }

    /**
     * 计算设备风险分数
     */
    private PredictionResult calculateRisk(String assetType, Long deviceId, Long factoryId, String username) {
        // 获取维护统计数据（maintenance_record.equipment_id 实际存储的是device_number）
        Map<String, Object> stats = predictionResultMapper.getEquipmentMaintenanceStats(deviceId);

        int r1Score = 0; // 距上次维护天数得分
        int r2Score = 0; // 近30天维护次数得分
        int r3Score = 0; // 近90天维护次数得分
        int r4Score = 0; // 关键词命中得分

        Integer daysSinceLast = null;
        Integer count30d = 0;
        Integer count90d = 0;
        boolean hasHistory = false;

        if (stats != null && stats.get("total_count") != null) {
            Long totalCount = getLongValue(stats.get("total_count"));
            hasHistory = totalCount != null && totalCount > 0;

            if (hasHistory) {
                daysSinceLast = getIntValue(stats.get("days_since_last"));
                count30d = getIntValue(stats.get("count_30d"));
                count90d = getIntValue(stats.get("count_90d"));

                // 规则R1：距上次维护天数
                if (daysSinceLast != null) {
                    if (daysSinceLast > 90) {
                        r1Score = 40;
                    } else if (daysSinceLast > 60) {
                        r1Score = 25;
                    } else if (daysSinceLast > 30) {
                        r1Score = 10;
                    }
                }

                // 规则R2：近30天维护次数
                if (count30d != null) {
                    if (count30d >= 3) {
                        r2Score = 35;
                    } else if (count30d == 2) {
                        r2Score = 20;
                    } else if (count30d == 1) {
                        r2Score = 8;
                    }
                }

                // 规则R3：近90天维护次数
                if (count90d != null) {
                    if (count90d >= 6) {
                        r3Score = 25;
                    } else if (count90d >= 3) {
                        r3Score = 12;
                    }
                }

                // 规则R4：关键词命中
                int keywordHit = predictionResultMapper.checkMaintenanceKeywords(deviceId);
                if (keywordHit > 0) {
                    r4Score = 15;
                }
            }
        }

        // 计算最终风险分数
        int riskScore = Math.min(100, r1Score + r2Score + r3Score + r4Score);

        // 确定风险等级
        String riskLevel;
        if (riskScore >= 80) {
            riskLevel = "high";
        } else if (riskScore >= 50) {
            riskLevel = "medium";
        } else {
            riskLevel = "low";
        }

        // 构建原因详情JSON
        JSONObject reasonDetail = new JSONObject();
        reasonDetail.put("days_since_last", daysSinceLast);
        reasonDetail.put("count_30d", count30d);
        reasonDetail.put("count_90d", count90d);
        reasonDetail.put("has_history", hasHistory);

        JSONObject rules = new JSONObject();
        rules.put("R1", buildRuleDetail("距上次维护天数", daysSinceLast, r1Score,
                daysSinceLast != null && daysSinceLast > 90 ? ">90天"
                        : daysSinceLast != null && daysSinceLast > 60 ? "60-90天"
                                : daysSinceLast != null && daysSinceLast > 30 ? "30-60天" : "<=30天"));
        rules.put("R2", buildRuleDetail("近30天维护次数", count30d, r2Score,
                count30d != null && count30d >= 3 ? ">=3次"
                        : count30d != null && count30d == 2 ? "2次" : count30d != null && count30d == 1 ? "1次" : "0次"));
        rules.put("R3", buildRuleDetail("近90天维护次数", count90d, r3Score,
                count90d != null && count90d >= 6 ? ">=6次" : count90d != null && count90d >= 3 ? "3-5次" : "0-2次"));
        rules.put("R4", buildRuleDetail("关键词命中", r4Score > 0 ? 1 : 0, r4Score,
                r4Score > 0 ? "命中" : "未命中"));
        reasonDetail.put("rules", rules);
        reasonDetail.put("total_score", riskScore);
        reasonDetail.put("risk_level", riskLevel);

        // 构建原因摘要
        String reasonSummary;
        if (!hasHistory) {
            reasonSummary = "无维护历史数据，建议补录巡检记录";
        } else {
            List<String> reasons = new ArrayList<>();
            if (r1Score >= 25) {
                reasons.add("距上次维护" + daysSinceLast + "天");
            }
            if (r2Score >= 20) {
                reasons.add("近30天维护" + count30d + "次");
            }
            if (r3Score >= 12) {
                reasons.add("近90天维护" + count90d + "次");
            }
            if (r4Score > 0) {
                reasons.add("维护记录含异常关键词");
            }
            if (reasons.isEmpty()) {
                reasonSummary = "设备运行状态正常";
            } else {
                reasonSummary = String.join("；", reasons);
            }
        }

        // 构建建议
        String recommendation;
        if ("high".equals(riskLevel)) {
            recommendation = "建议48小时内安排检查，重点检查易损件、温升、润滑、紧固状态，必要时停机检修";
        } else if ("medium".equals(riskLevel)) {
            recommendation = "建议7天内安排巡检与预防性保养";
        } else {
            if (!hasHistory) {
                recommendation = "建议补录设备维护记录，纳入日常巡检计划";
            } else {
                recommendation = "建议纳入例行保养周期，持续观察";
            }
        }

        // 创建预测结果
        PredictionResult prediction = new PredictionResult();
        prediction.setFactoryId(factoryId);
        prediction.setAssetType(assetType);
        prediction.setAssetId(deviceId);
        prediction.setPredictionTime(new Date());
        prediction.setRiskLevel(riskLevel);
        prediction.setRiskScore(riskScore);
        prediction.setReasonSummary(reasonSummary);
        prediction.setReasonDetailJson(reasonDetail.toJSONString());
        prediction.setRecommendation(recommendation);
        prediction.setStatus("new");
        prediction.setModelVersion(MODEL_VERSION);
        prediction.setCreateBy(username);

        return prediction;
    }

    /**
     * 构建规则详情
     */
    private JSONObject buildRuleDetail(String name, Object value, int score, String threshold) {
        JSONObject rule = new JSONObject();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("score", score);
        rule.put("threshold", threshold);
        return rule;
    }

    /**
     * 安全获取Long值
     */
    private Long getLongValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全获取Integer值
     */
    private Integer getIntValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof Long) {
            return ((Long) obj).intValue();
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
