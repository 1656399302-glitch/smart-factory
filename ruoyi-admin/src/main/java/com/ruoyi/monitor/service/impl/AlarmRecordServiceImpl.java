package com.ruoyi.monitor.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.monitor.mapper.AlarmRecordMapper;
import com.ruoyi.monitor.domain.AlarmRecord;
import com.ruoyi.monitor.service.IAlarmRecordService;

/**
 * 报警记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@Service
public class AlarmRecordServiceImpl implements IAlarmRecordService {
    @Autowired
    private AlarmRecordMapper alarmRecordMapper;

    @Override
    public AlarmRecord selectAlarmRecordByAlarmId(Long alarmId) {
        return alarmRecordMapper.selectAlarmRecordByAlarmId(alarmId);
    }

    @Override
    public List<AlarmRecord> selectAlarmRecordList(AlarmRecord alarmRecord) {
        return alarmRecordMapper.selectAlarmRecordList(alarmRecord);
    }

    @Override
    public int insertAlarmRecord(AlarmRecord alarmRecord) {
        return alarmRecordMapper.insertAlarmRecord(alarmRecord);
    }

    @Override
    public int updateAlarmRecord(AlarmRecord alarmRecord) {
        return alarmRecordMapper.updateAlarmRecord(alarmRecord);
    }

    /**
     * 确认报警（new -> acknowledged）
     */
    @Override
    public int ackAlarmRecord(Long alarmId, String handledBy) {
        AlarmRecord record = new AlarmRecord();
        record.setAlarmId(alarmId);
        record.setStatus("acknowledged");
        record.setHandledBy(handledBy);
        record.setHandledTime(new Date());
        return alarmRecordMapper.updateAlarmRecord(record);
    }

    /**
     * 关闭报警（-> resolved）
     */
    @Override
    public int resolveAlarmRecord(Long alarmId, String handledBy, String remark) {
        AlarmRecord record = new AlarmRecord();
        record.setAlarmId(alarmId);
        record.setStatus("resolved");
        record.setHandledBy(handledBy);
        record.setHandledTime(new Date());
        record.setRemark(remark);
        return alarmRecordMapper.updateAlarmRecord(record);
    }

    @Override
    public int deleteAlarmRecordByAlarmIds(Long[] alarmIds) {
        return alarmRecordMapper.deleteAlarmRecordByAlarmIds(alarmIds);
    }

    @Override
    public int deleteAlarmRecordByAlarmId(Long alarmId) {
        return alarmRecordMapper.deleteAlarmRecordByAlarmId(alarmId);
    }
}
