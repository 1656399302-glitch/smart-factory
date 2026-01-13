package com.ruoyi.predict.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.predict.domain.PredictionResult;
import com.ruoyi.predict.service.IPredictionResultService;
import com.ruoyi.ar.domain.MaintenanceRecord;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预测结果Controller
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@RestController
@RequestMapping("/predict/result")
public class PredictionResultController extends BaseController {
    @Autowired
    private IPredictionResultService predictionResultService;

    /**
     * 查询预测结果列表
     */
    @PreAuthorize("@ss.hasPermi('predict:result:list')")
    @GetMapping("/list")
    public TableDataInfo list(PredictionResult predictionResult) {
        startPage();
        List<PredictionResult> list = predictionResultService.selectPredictionResultList(predictionResult);
        return getDataTable(list);
    }

    /**
     * 导出预测结果列表
     */
    @PreAuthorize("@ss.hasPermi('predict:result:export')")
    @Log(title = "预测结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PredictionResult predictionResult) {
        List<PredictionResult> list = predictionResultService.selectPredictionResultList(predictionResult);
        ExcelUtil<PredictionResult> util = new ExcelUtil<PredictionResult>(PredictionResult.class);
        util.exportExcel(response, list, "预测结果数据");
    }

    /**
     * 获取预测结果详细信息
     */
    @PreAuthorize("@ss.hasPermi('predict:result:view')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(predictionResultService.selectPredictionResultById(id));
    }

    /**
     * 获取资产的维护记录列表
     */
    @PreAuthorize("@ss.hasPermi('predict:result:view')")
    @GetMapping(value = "/maintenance/{assetType}/{assetId}")
    public AjaxResult getMaintenanceRecords(@PathVariable("assetType") String assetType,
            @PathVariable("assetId") Long assetId) {
        List<MaintenanceRecord> list = predictionResultService.getMaintenanceRecordsByAsset(assetType, assetId);
        return success(list);
    }

    /**
     * 确认预测结果
     */
    @PreAuthorize("@ss.hasPermi('predict:result:handle')")
    @Log(title = "预测结果", businessType = BusinessType.UPDATE)
    @PostMapping("/ack/{id}")
    public AjaxResult acknowledge(@PathVariable("id") Long id) {
        int result = predictionResultService.acknowledgePredictionResult(id, getUsername());
        if (result > 0) {
            return success("确认成功");
        }
        return error("确认失败，请检查状态是否正确");
    }

    /**
     * 忽略预测结果
     */
    @PreAuthorize("@ss.hasPermi('predict:result:handle')")
    @Log(title = "预测结果", businessType = BusinessType.UPDATE)
    @PostMapping("/ignore/{id}")
    public AjaxResult ignore(@PathVariable("id") Long id) {
        int result = predictionResultService.ignorePredictionResult(id, getUsername());
        if (result > 0) {
            return success("已忽略");
        }
        return error("操作失败，请检查状态是否正确");
    }

    /**
     * 标记预测结果为已完成
     */
    @PreAuthorize("@ss.hasPermi('predict:result:handle')")
    @Log(title = "预测结果", businessType = BusinessType.UPDATE)
    @PostMapping("/resolve/{id}")
    public AjaxResult resolve(@PathVariable("id") Long id) {
        int result = predictionResultService.resolvePredictionResult(id, getUsername());
        if (result > 0) {
            return success("已完成");
        }
        return error("操作失败，请检查状态是否正确");
    }

    /**
     * 删除预测结果
     */
    @PreAuthorize("@ss.hasPermi('predict:result:remove')")
    @Log(title = "预测结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(predictionResultService.deletePredictionResultByIds(ids));
    }
}
