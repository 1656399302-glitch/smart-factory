package com.ruoyi.monitor.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.monitor.mapper.SensorDataMapper;
import com.ruoyi.monitor.mapper.DeviceSensorMapper;
import com.ruoyi.monitor.mapper.AlarmRuleMapper;
import com.ruoyi.monitor.mapper.AlarmRecordMapper;
import com.ruoyi.monitor.domain.SensorData;
import com.ruoyi.monitor.domain.DeviceSensor;
import com.ruoyi.monitor.domain.AlarmRule;
import com.ruoyi.monitor.domain.AlarmRecord;
import com.ruoyi.monitor.service.ISensorDataService;

/**
 * 传感器数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@Service
public class SensorDataServiceImpl implements ISensorDataService {
    private static final Logger log = LoggerFactory.getLogger(SensorDataServiceImpl.class);

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Autowired
    private DeviceSensorMapper deviceSensorMapper;

    @Autowired
    private AlarmRuleMapper alarmRuleMapper;

    @Autowired
    private AlarmRecordMapper alarmRecordMapper;

    @Override
    public SensorData selectSensorDataById(Long id) {
        return sensorDataMapper.selectSensorDataById(id);
    }

    @Override
    public List<SensorData> selectSensorDataList(SensorData sensorData) {
        return sensorDataMapper.selectSensorDataList(sensorData);
    }

    @Override
    public List<SensorData> selectLatestDataByDeviceNumber(Long deviceNumber) {
        return sensorDataMapper.selectLatestDataByDeviceNumber(deviceNumber);
    }

    /**
     * 采集数据上报（核心方法）
     */
    @Override
    @Transactional
    public Map<String, Object> collectData(Long deviceNumber, String sensorCode,
            BigDecimal value, Date dataTime) {
        Map<String, Object> result = new HashMap<>();

        // 1. 根据 device_number + sensor_code 查找 sensor
        DeviceSensor sensor = deviceSensorMapper.selectByDeviceNumberAndSensorCode(deviceNumber, sensorCode);
        if (sensor == null) {
            result.put("success", false);
            result.put("message", "传感器不存在: deviceNumber=" + deviceNumber + ", sensorCode=" + sensorCode);
            return result;
        }

        // 检查传感器是否启用
        if ("1".equals(sensor.getStatus())) {
            result.put("success", false);
            result.put("message", "传感器已停用: " + sensorCode);
            return result;
        }

        // 2. 写入 sensor_data
        SensorData sensorData = new SensorData();
        sensorData.setSensorId(sensor.getSensorId());
        sensorData.setDeviceNumber(deviceNumber);
        sensorData.setValue(value);
        sensorData.setDataTime(dataTime != null ? dataTime : new Date());
        sensorDataMapper.insertSensorData(sensorData);

        // 3. 查询该传感器的启用规则，逐条做阈值判断
        List<AlarmRule> rules = alarmRuleMapper.selectEnabledRulesBySensorId(sensor.getSensorId());
        int alarmCount = 0;
        for (AlarmRule rule : rules) {
            try {
                boolean triggered = checkAlarmRule(rule, value);
                if (triggered) {
                    // 检查是否已有活动告警（去重）
                    AlarmRecord activeAlarm = alarmRecordMapper.selectActiveAlarm(
                            rule.getRuleId(), sensor.getSensorId());
                    if (activeAlarm != null) {
                        // 更新现有告警的时间和值
                        activeAlarm.setAlarmTime(sensorData.getDataTime());
                        activeAlarm.setAlarmValue(value);
                        alarmRecordMapper.updateAlarmRecord(activeAlarm);
                        log.info("更新活动告警: alarmId={}, ruleId={}, value={}",
                                activeAlarm.getAlarmId(), rule.getRuleId(), value);
                    } else {
                        // 创建新的报警记录
                        AlarmRecord alarmRecord = new AlarmRecord();
                        alarmRecord.setRuleId(rule.getRuleId());
                        alarmRecord.setDeviceNumber(deviceNumber);
                        alarmRecord.setSensorId(sensor.getSensorId());
                        alarmRecord.setAlarmTime(sensorData.getDataTime());
                        alarmRecord.setAlarmValue(value);
                        alarmRecord.setSeverity(rule.getSeverity());
                        alarmRecord.setStatus("new");
                        alarmRecordMapper.insertAlarmRecord(alarmRecord);
                        alarmCount++;
                        log.info("触发新报警: ruleId={}, ruleName={}, value={}, severity={}",
                                rule.getRuleId(), rule.getRuleName(), value, rule.getSeverity());
                    }
                }
            } catch (Exception e) {
                log.error("检查报警规则失败: ruleId=" + rule.getRuleId(), e);
            }
        }

        result.put("success", true);
        result.put("message", "数据上报成功");
        result.put("sensorId", sensor.getSensorId());
        result.put("dataId", sensorData.getId());
        result.put("alarmCount", alarmCount);
        return result;
    }

    /**
     * 检查是否触发报警规则
     */
    private boolean checkAlarmRule(AlarmRule rule, BigDecimal value) {
        String compareType = rule.getCompareType();
        BigDecimal min = rule.getThresholdMin();
        BigDecimal max = rule.getThresholdMax();

        switch (compareType) {
            case "gt":
                // value > threshold_max
                if (max == null)
                    return false;
                return value.compareTo(max) > 0;
            case "ge":
                // value >= threshold_max
                if (max == null)
                    return false;
                return value.compareTo(max) >= 0;
            case "lt":
                // value < threshold_min
                if (min == null)
                    return false;
                return value.compareTo(min) < 0;
            case "le":
                // value <= threshold_min
                if (min == null)
                    return false;
                return value.compareTo(min) <= 0;
            case "between":
                // value < threshold_min OR value > threshold_max（超出区间即报警）
                if (min == null || max == null)
                    return false;
                return value.compareTo(min) < 0 || value.compareTo(max) > 0;
            default:
                log.warn("未知的比较类型: {}", compareType);
                return false;
        }
    }

    @Override
    public int insertSensorData(SensorData sensorData) {
        return sensorDataMapper.insertSensorData(sensorData);
    }

    @Override
    public int deleteSensorDataById(Long id) {
        return sensorDataMapper.deleteSensorDataById(id);
    }

    @Override
    public int deleteSensorDataByIds(Long[] ids) {
        return sensorDataMapper.deleteSensorDataByIds(ids);
    }
}
