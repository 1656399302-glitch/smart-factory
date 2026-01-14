package com.ruoyi.monitor.service;

import java.util.List;
import com.ruoyi.monitor.domain.AlarmRecord;

/**
 * 报警记录Service接口
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
public interface IAlarmRecordService {
    /**
     * 查询报警记录
     */
    public AlarmRecord selectAlarmRecordByAlarmId(Long alarmId);

    /**
     * 查询报警记录列表
     */
    public List<AlarmRecord> selectAlarmRecordList(AlarmRecord alarmRecord);

    /**
     * 新增报警记录
     */
    public int insertAlarmRecord(AlarmRecord alarmRecord);

    /**
     * 修改报警记录
     */
    public int updateAlarmRecord(AlarmRecord alarmRecord);

    /**
     * 确认报警（new -> acknowledged）
     */
    public int ackAlarmRecord(Long alarmId, String handledBy);

    /**
     * 关闭报警（-> resolved）
     */
    public int resolveAlarmRecord(Long alarmId, String handledBy, String remark);

    /**
     * 批量删除报警记录
     */
    public int deleteAlarmRecordByAlarmIds(Long[] alarmIds);

    /**
     * 删除报警记录信息
     */
    public int deleteAlarmRecordByAlarmId(Long alarmId);
}
