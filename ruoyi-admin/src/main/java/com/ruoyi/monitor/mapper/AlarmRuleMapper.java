package com.ruoyi.monitor.mapper;

import java.util.List;
import com.ruoyi.monitor.domain.AlarmRule;

/**
 * 报警规则Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public interface AlarmRuleMapper {
    /**
     * 查询报警规则
     */
    public AlarmRule selectAlarmRuleByRuleId(Long ruleId);

    /**
     * 查询报警规则列表
     */
    public List<AlarmRule> selectAlarmRuleList(AlarmRule alarmRule);

    /**
     * 查询指定传感器的启用规则列表
     */
    public List<AlarmRule> selectEnabledRulesBySensorId(Long sensorId);

    /**
     * 新增报警规则
     */
    public int insertAlarmRule(AlarmRule alarmRule);

    /**
     * 修改报警规则
     */
    public int updateAlarmRule(AlarmRule alarmRule);

    /**
     * 删除报警规则
     */
    public int deleteAlarmRuleByRuleId(Long ruleId);

    /**
     * 批量删除报警规则
     */
    public int deleteAlarmRuleByRuleIds(Long[] ruleIds);
}
