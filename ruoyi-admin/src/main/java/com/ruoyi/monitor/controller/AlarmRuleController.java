package com.ruoyi.monitor.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.monitor.domain.AlarmRule;
import com.ruoyi.monitor.service.IAlarmRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报警规则Controller
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/monitor/rule")
public class AlarmRuleController extends BaseController {
    @Autowired
    private IAlarmRuleService alarmRuleService;

    /**
     * 查询报警规则列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlarmRule alarmRule) {
        startPage();
        List<AlarmRule> list = alarmRuleService.selectAlarmRuleList(alarmRule);
        return getDataTable(list);
    }

    /**
     * 导出报警规则列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:rule:export')")
    @Log(title = "报警规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlarmRule alarmRule) {
        List<AlarmRule> list = alarmRuleService.selectAlarmRuleList(alarmRule);
        ExcelUtil<AlarmRule> util = new ExcelUtil<AlarmRule>(AlarmRule.class);
        util.exportExcel(response, list, "报警规则数据");
    }

    /**
     * 获取报警规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('monitor:rule:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId) {
        return success(alarmRuleService.selectAlarmRuleByRuleId(ruleId));
    }

    /**
     * 新增报警规则
     */
    @PreAuthorize("@ss.hasPermi('monitor:rule:add')")
    @Log(title = "报警规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlarmRule alarmRule) {
        alarmRule.setCreateBy(getUsername());
        return toAjax(alarmRuleService.insertAlarmRule(alarmRule));
    }

    /**
     * 修改报警规则
     */
    @PreAuthorize("@ss.hasPermi('monitor:rule:edit')")
    @Log(title = "报警规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlarmRule alarmRule) {
        alarmRule.setUpdateBy(getUsername());
        return toAjax(alarmRuleService.updateAlarmRule(alarmRule));
    }

    /**
     * 删除报警规则
     */
    @PreAuthorize("@ss.hasPermi('monitor:rule:remove')")
    @Log(title = "报警规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds) {
        return toAjax(alarmRuleService.deleteAlarmRuleByRuleIds(ruleIds));
    }
}
