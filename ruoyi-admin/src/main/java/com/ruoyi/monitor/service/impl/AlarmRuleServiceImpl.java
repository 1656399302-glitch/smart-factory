package com.ruoyi.monitor.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.monitor.mapper.AlarmRuleMapper;
import com.ruoyi.monitor.domain.AlarmRule;
import com.ruoyi.monitor.service.IAlarmRuleService;

/**
 * 报警规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@Service
public class AlarmRuleServiceImpl implements IAlarmRuleService {
    @Autowired
    private AlarmRuleMapper alarmRuleMapper;

    @Override
    public AlarmRule selectAlarmRuleByRuleId(Long ruleId) {
        return alarmRuleMapper.selectAlarmRuleByRuleId(ruleId);
    }

    @Override
    public List<AlarmRule> selectAlarmRuleList(AlarmRule alarmRule) {
        return alarmRuleMapper.selectAlarmRuleList(alarmRule);
    }

    @Override
    public List<AlarmRule> selectEnabledRulesBySensorId(Long sensorId) {
        return alarmRuleMapper.selectEnabledRulesBySensorId(sensorId);
    }

    @Override
    public int insertAlarmRule(AlarmRule alarmRule) {
        return alarmRuleMapper.insertAlarmRule(alarmRule);
    }

    @Override
    public int updateAlarmRule(AlarmRule alarmRule) {
        return alarmRuleMapper.updateAlarmRule(alarmRule);
    }

    @Override
    public int deleteAlarmRuleByRuleIds(Long[] ruleIds) {
        return alarmRuleMapper.deleteAlarmRuleByRuleIds(ruleIds);
    }

    @Override
    public int deleteAlarmRuleByRuleId(Long ruleId) {
        return alarmRuleMapper.deleteAlarmRuleByRuleId(ruleId);
    }
}
