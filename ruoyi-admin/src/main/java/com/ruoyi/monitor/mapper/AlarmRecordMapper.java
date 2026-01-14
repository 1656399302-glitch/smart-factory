package com.ruoyi.monitor.mapper;

import java.util.List;
import com.ruoyi.monitor.domain.AlarmRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 报警记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public interface AlarmRecordMapper {
    /**
     * 查询报警记录
     */
    public AlarmRecord selectAlarmRecordByAlarmId(Long alarmId);

    /**
     * 查询报警记录列表
     */
    public List<AlarmRecord> selectAlarmRecordList(AlarmRecord alarmRecord);

    /**
     * 查询活动告警（未解决的）
     */
    public AlarmRecord selectActiveAlarm(@Param("ruleId") Long ruleId, @Param("sensorId") Long sensorId);

    /**
     * 新增报警记录
     */
    public int insertAlarmRecord(AlarmRecord alarmRecord);

    /**
     * 修改报警记录
     */
    public int updateAlarmRecord(AlarmRecord alarmRecord);

    /**
     * 删除报警记录
     */
    public int deleteAlarmRecordByAlarmId(Long alarmId);

    /**
     * 批量删除报警记录
     */
    public int deleteAlarmRecordByAlarmIds(Long[] alarmIds);
}
