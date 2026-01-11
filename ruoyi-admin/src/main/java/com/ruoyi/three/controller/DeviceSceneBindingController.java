package com.ruoyi.three.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.three.domain.DeviceSceneBinding;
import com.ruoyi.three.service.IDeviceSceneBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点位绑定Controller（M1）
 */
@RestController
@RequestMapping("/three/deviceSceneBinding")
public class DeviceSceneBindingController extends BaseController
{
    @Autowired
    private IDeviceSceneBindingService deviceSceneBindingService;

    /**
     * 查询点位绑定列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('three:binding:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceSceneBinding deviceSceneBinding)
    {
        startPage();
        List<DeviceSceneBinding> list = deviceSceneBindingService.selectDeviceSceneBindingList(deviceSceneBinding);
        return getDataTable(list);
    }

    /**
     * 获取点位绑定详情
     */
    @PreAuthorize("@ss.hasPermi('three:binding:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(deviceSceneBindingService.selectDeviceSceneBindingById(id));
    }

    /**
     * 新增点位绑定（M1：仅保存坐标/旋转/缩放）
     */
    @PreAuthorize("@ss.hasPermi('three:binding:add')")
    @Log(title = "点位绑定", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceSceneBinding deviceSceneBinding)
    {
        deviceSceneBinding.setCreateBy(getUsername());
        return toAjax(deviceSceneBindingService.insertDeviceSceneBinding(deviceSceneBinding));
    }

    /**
     * 修改点位绑定
     */
    @PreAuthorize("@ss.hasPermi('three:binding:edit')")
    @Log(title = "点位绑定", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceSceneBinding deviceSceneBinding)
    {
        deviceSceneBinding.setUpdateBy(getUsername());
        return toAjax(deviceSceneBindingService.updateDeviceSceneBinding(deviceSceneBinding));
    }

    /**
     * 删除点位绑定
     */
    @PreAuthorize("@ss.hasPermi('three:binding:remove')")
    @Log(title = "点位绑定", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(deviceSceneBindingService.deleteDeviceSceneBindingByIds(ids));
    }
}


