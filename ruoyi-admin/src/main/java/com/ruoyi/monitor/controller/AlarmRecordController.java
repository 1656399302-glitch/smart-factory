package com.ruoyi.monitor.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.monitor.domain.AlarmRecord;
import com.ruoyi.monitor.service.IAlarmRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报警记录Controller
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/monitor/alarm")
public class AlarmRecordController extends BaseController {
    @Autowired
    private IAlarmRecordService alarmRecordService;

    /**
     * 查询报警记录列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:alarm:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlarmRecord alarmRecord) {
        startPage();
        List<AlarmRecord> list = alarmRecordService.selectAlarmRecordList(alarmRecord);
        return getDataTable(list);
    }

    /**
     * 导出报警记录列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:alarm:export')")
    @Log(title = "报警记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlarmRecord alarmRecord) {
        List<AlarmRecord> list = alarmRecordService.selectAlarmRecordList(alarmRecord);
        ExcelUtil<AlarmRecord> util = new ExcelUtil<AlarmRecord>(AlarmRecord.class);
        util.exportExcel(response, list, "报警记录数据");
    }

    /**
     * 获取报警记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('monitor:alarm:query')")
    @GetMapping(value = "/{alarmId}")
    public AjaxResult getInfo(@PathVariable("alarmId") Long alarmId) {
        return success(alarmRecordService.selectAlarmRecordByAlarmId(alarmId));
    }

    /**
     * 确认报警（new -> acknowledged）
     */
    @PreAuthorize("@ss.hasPermi('monitor:alarm:handle')")
    @Log(title = "报警确认", businessType = BusinessType.UPDATE)
    @PostMapping("/ack/{alarmId}")
    public AjaxResult ack(@PathVariable Long alarmId) {
        return toAjax(alarmRecordService.ackAlarmRecord(alarmId, getUsername()));
    }

    /**
     * 关闭报警（-> resolved）
     */
    @PreAuthorize("@ss.hasPermi('monitor:alarm:handle')")
    @Log(title = "报警关闭", businessType = BusinessType.UPDATE)
    @PostMapping("/resolve/{alarmId}")
    public AjaxResult resolve(@PathVariable Long alarmId, @RequestBody(required = false) ResolveRequest request) {
        String remark = request != null ? request.getRemark() : null;
        return toAjax(alarmRecordService.resolveAlarmRecord(alarmId, getUsername(), remark));
    }

    /**
     * 关闭请求参数
     */
    public static class ResolveRequest {
        private String remark;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
