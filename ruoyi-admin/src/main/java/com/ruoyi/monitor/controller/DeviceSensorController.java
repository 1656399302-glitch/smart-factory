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
import com.ruoyi.monitor.domain.DeviceSensor;
import com.ruoyi.monitor.service.IDeviceSensorService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备传感器Controller
 * 
 * @author ruoyi
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/monitor/sensor")
public class DeviceSensorController extends BaseController {
    @Autowired
    private IDeviceSensorService deviceSensorService;

    /**
     * 查询设备传感器列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:sensor:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceSensor deviceSensor) {
        startPage();
        List<DeviceSensor> list = deviceSensorService.selectDeviceSensorList(deviceSensor);
        return getDataTable(list);
    }

    /**
     * 查询指定设备的传感器（不分页，用于下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('monitor:sensor:query')")
    @GetMapping("/listByDevice/{deviceNumber}")
    public AjaxResult listByDevice(@PathVariable Long deviceNumber) {
        List<DeviceSensor> list = deviceSensorService.selectByDeviceNumber(deviceNumber);
        return success(list);
    }

    /**
     * 导出设备传感器列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:sensor:export')")
    @Log(title = "设备传感器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceSensor deviceSensor) {
        List<DeviceSensor> list = deviceSensorService.selectDeviceSensorList(deviceSensor);
        ExcelUtil<DeviceSensor> util = new ExcelUtil<DeviceSensor>(DeviceSensor.class);
        util.exportExcel(response, list, "设备传感器数据");
    }

    /**
     * 获取设备传感器详细信息
     */
    @PreAuthorize("@ss.hasPermi('monitor:sensor:query')")
    @GetMapping(value = "/{sensorId}")
    public AjaxResult getInfo(@PathVariable("sensorId") Long sensorId) {
        return success(deviceSensorService.selectDeviceSensorBySensorId(sensorId));
    }

    /**
     * 新增设备传感器
     */
    @PreAuthorize("@ss.hasPermi('monitor:sensor:add')")
    @Log(title = "设备传感器", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceSensor deviceSensor) {
        deviceSensor.setCreateBy(getUsername());
        return toAjax(deviceSensorService.insertDeviceSensor(deviceSensor));
    }

    /**
     * 修改设备传感器
     */
    @PreAuthorize("@ss.hasPermi('monitor:sensor:edit')")
    @Log(title = "设备传感器", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceSensor deviceSensor) {
        deviceSensor.setUpdateBy(getUsername());
        return toAjax(deviceSensorService.updateDeviceSensor(deviceSensor));
    }

    /**
     * 删除设备传感器
     */
    @PreAuthorize("@ss.hasPermi('monitor:sensor:remove')")
    @Log(title = "设备传感器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sensorIds}")
    public AjaxResult remove(@PathVariable Long[] sensorIds) {
        return toAjax(deviceSensorService.deleteDeviceSensorBySensorIds(sensorIds));
    }
}
