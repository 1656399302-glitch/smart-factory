package com.ruoyi.monitor.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.monitor.domain.SensorData;
import com.ruoyi.monitor.service.ISensorDataService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 传感器数据Controller
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/monitor/data")
public class SensorDataController extends BaseController {
    @Autowired
    private ISensorDataService sensorDataService;

    /**
     * 查询传感器数据列表（历史数据）
     */
    @PreAuthorize("@ss.hasPermi('monitor:data:view')")
    @GetMapping("/list")
    public TableDataInfo list(SensorData sensorData) {
        startPage();
        List<SensorData> list = sensorDataService.selectSensorDataList(sensorData);
        return getDataTable(list);
    }

    /**
     * 获取指定设备各传感器的最新数据
     */
    @PreAuthorize("@ss.hasPermi('monitor:data:view')")
    @GetMapping("/latest")
    public AjaxResult latest(@RequestParam Long deviceNumber) {
        List<SensorData> list = sensorDataService.selectLatestDataByDeviceNumber(deviceNumber);
        return success(list);
    }

    /**
     * 导出传感器数据列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:data:view')")
    @Log(title = "传感器数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SensorData sensorData) {
        List<SensorData> list = sensorDataService.selectSensorDataList(sensorData);
        ExcelUtil<SensorData> util = new ExcelUtil<SensorData>(SensorData.class);
        util.exportExcel(response, list, "传感器数据");
    }

    /**
     * 删除传感器数据
     */
    @PreAuthorize("@ss.hasPermi('monitor:data:remove')")
    @Log(title = "传感器数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sensorDataService.deleteSensorDataByIds(ids));
    }

    /**
     * 采集上报接口（对设备/网关）
     * P0采用HTTP上报，可选简单鉴权（请求头X-Token）
     */
    @Log(title = "传感器数据采集", businessType = BusinessType.INSERT)
    @PostMapping("/collect")
    public AjaxResult collect(@RequestBody CollectRequest request) {
        // 参数校验
        if (request.getDeviceNumber() == null) {
            return error("设备编号不能为空");
        }
        if (request.getSensorCode() == null || request.getSensorCode().isEmpty()) {
            return error("传感器编码不能为空");
        }
        if (request.getValue() == null) {
            return error("采集值不能为空");
        }

        // 调用采集服务
        Map<String, Object> result = sensorDataService.collectData(
                request.getDeviceNumber(),
                request.getSensorCode(),
                request.getValue(),
                request.getDataTime());

        Boolean success = (Boolean) result.get("success");
        if (success != null && success) {
            AjaxResult ajaxResult = AjaxResult.success((String) result.get("message"));
            ajaxResult.put("data", result);
            return ajaxResult;
        } else {
            return error((String) result.get("message"));
        }
    }

    /**
     * 采集请求参数
     */
    public static class CollectRequest {
        /** 设备编号 */
        private Long deviceNumber;
        /** 传感器编码 */
        private String sensorCode;
        /** 采集值 */
        private BigDecimal value;
        /** 采集时间（可选） */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date dataTime;

        public Long getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(Long deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public String getSensorCode() {
            return sensorCode;
        }

        public void setSensorCode(String sensorCode) {
            this.sensorCode = sensorCode;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public Date getDataTime() {
            return dataTime;
        }

        public void setDataTime(Date dataTime) {
            this.dataTime = dataTime;
        }
    }
}
