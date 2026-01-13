package com.ruoyi.predict.controller;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.predict.service.IPredictionEngineService;

/**
 * 预测任务Controller
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@RestController
@RequestMapping("/predict/task")
public class PredictionTaskController extends BaseController {
    @Autowired
    private IPredictionEngineService predictionEngineService;

    /**
     * 运行预测任务
     */
    @PreAuthorize("@ss.hasPermi('predict:task:run')")
    @Log(title = "预测任务", businessType = BusinessType.INSERT)
    @PostMapping("/run")
    public AjaxResult run(@RequestBody(required = false) PredictionTaskRequest request) {
        String assetType = "equipment"; // P0默认为equipment
        Long factoryId = null;
        List<Long> assetIds = null;

        if (request != null) {
            if (request.getAssetType() != null && !request.getAssetType().isEmpty()) {
                assetType = request.getAssetType();
            }
            factoryId = request.getFactoryId();
            assetIds = request.getAssetIds();
        }

        Map<String, Object> result = predictionEngineService.runPrediction(assetType, factoryId, assetIds,
                getUsername());

        Boolean success = (Boolean) result.get("success");
        if (success != null && success) {
            AjaxResult ajaxResult = AjaxResult.success((String) result.get("message"));
            ajaxResult.put("data", result);
            return ajaxResult;
        } else {
            AjaxResult ajaxResult = AjaxResult.error((String) result.get("message"));
            ajaxResult.put("data", result);
            return ajaxResult;
        }

    }

    /**
     * 预测任务请求参数
     */
    public static class PredictionTaskRequest {
        /** 资产类型：equipment/device */
        private String assetType;

        /** 工厂ID（可选） */
        private Long factoryId;

        /** 资产ID列表（可选） */
        private List<Long> assetIds;

        public String getAssetType() {
            return assetType;
        }

        public void setAssetType(String assetType) {
            this.assetType = assetType;
        }

        public Long getFactoryId() {
            return factoryId;
        }

        public void setFactoryId(Long factoryId) {
            this.factoryId = factoryId;
        }

        public List<Long> getAssetIds() {
            return assetIds;
        }

        public void setAssetIds(List<Long> assetIds) {
            this.assetIds = assetIds;
        }
    }
}
