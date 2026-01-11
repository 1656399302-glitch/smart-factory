package com.ruoyi.three.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.three.domain.FactoryModel;
import com.ruoyi.three.service.IFactoryModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工厂模型Controller（M1）
 */
@RestController
@RequestMapping("/three/factoryModel")
public class FactoryModelController extends BaseController
{
    @Autowired
    private IFactoryModelService factoryModelService;

    /**
     * 查询工厂模型列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('three:factoryModel:list')")
    @GetMapping("/list")
    public TableDataInfo list(FactoryModel factoryModel)
    {
        startPage();
        List<FactoryModel> list = factoryModelService.selectFactoryModelList(factoryModel);
        return getDataTable(list);
    }

    /**
     * 根据工厂ID查询模型列表（不分页，给下拉/选择用）
     */
    @PreAuthorize("@ss.hasPermi('three:factoryModel:list')")
    @GetMapping("/byFactory/{factoryId}")
    public AjaxResult byFactory(@PathVariable("factoryId") Long factoryId)
    {
        return success(factoryModelService.selectFactoryModelByFactoryId(factoryId));
    }

    /**
     * 获取工厂模型详情
     */
    @PreAuthorize("@ss.hasPermi('three:factoryModel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(factoryModelService.selectFactoryModelById(id));
    }

    /**
     * 新增工厂模型
     */
    @PreAuthorize("@ss.hasPermi('three:factoryModel:add')")
    @Log(title = "工厂模型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FactoryModel factoryModel)
    {
        factoryModel.setCreateBy(getUsername());
        return toAjax(factoryModelService.insertFactoryModel(factoryModel));
    }

    /**
     * 修改工厂模型
     */
    @PreAuthorize("@ss.hasPermi('three:factoryModel:edit')")
    @Log(title = "工厂模型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FactoryModel factoryModel)
    {
        factoryModel.setUpdateBy(getUsername());
        return toAjax(factoryModelService.updateFactoryModel(factoryModel));
    }

    /**
     * 删除工厂模型
     */
    @PreAuthorize("@ss.hasPermi('three:factoryModel:remove')")
    @Log(title = "工厂模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(factoryModelService.deleteFactoryModelByIds(ids));
    }

    /**
     * 发布工厂模型（同 factory 仅允许一个 published）
     */
    @PreAuthorize("@ss.hasPermi('three:factoryModel:publish')")
    @Log(title = "工厂模型发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/publish")
    public AjaxResult publish(@PathVariable("id") Long id)
    {
        factoryModelService.publishFactoryModel(id);
        return AjaxResult.success();
    }
}


