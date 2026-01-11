package com.ruoyi.three.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.three.domain.DeviceModel;
import com.ruoyi.three.service.IDeviceModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备模型Controller（M1）
 */
@RestController
@RequestMapping("/three/deviceModel")
public class DeviceModelController extends BaseController
{
    @Autowired
    private IDeviceModelService deviceModelService;

    /**
     * 查询设备模型列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('three:deviceModel:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceModel deviceModel)
    {
        startPage();
        List<DeviceModel> list = deviceModelService.selectDeviceModelList(deviceModel);
        return getDataTable(list);
    }

    /**
     * 获取设备模型详情
     */
    @PreAuthorize("@ss.hasPermi('three:deviceModel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(deviceModelService.selectDeviceModelById(id));
    }

    /**
     * 新增设备模型
     */
    @PreAuthorize("@ss.hasPermi('three:deviceModel:add')")
    @Log(title = "设备模型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceModel deviceModel)
    {
        deviceModel.setCreateBy(getUsername());
        return toAjax(deviceModelService.insertDeviceModel(deviceModel));
    }

    /**
     * 修改设备模型
     */
    @PreAuthorize("@ss.hasPermi('three:deviceModel:edit')")
    @Log(title = "设备模型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceModel deviceModel)
    {
        deviceModel.setUpdateBy(getUsername());
        return toAjax(deviceModelService.updateDeviceModel(deviceModel));
    }

    /**
     * 删除设备模型
     */
    @PreAuthorize("@ss.hasPermi('three:deviceModel:remove')")
    @Log(title = "设备模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(deviceModelService.deleteDeviceModelByIds(ids));
    }
}


