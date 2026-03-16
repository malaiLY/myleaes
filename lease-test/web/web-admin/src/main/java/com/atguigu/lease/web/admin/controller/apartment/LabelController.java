package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.service.LabelInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/admin/label")
public class LabelController {

    @Autowired
    private LabelInfoService service;

    @Operation(summary = "（根据类型）查询标签列表")
    @GetMapping("list")
    public Result<List<LabelInfo>> labelList(@RequestParam(required = false) ItemType type) {
        // 构造查询条件：管理端按标签类型筛选标签（供创建/编辑公寓或房源时选择使用）
        // ItemType 枚举表示标签适用于的业务对象类型（例如公寓、房源等）
        LambdaQueryWrapper<LabelInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 如果前端传入了 type，则仅查询该类型的标签；若未传入，则返回所有标签（用于标签管理页面展示）
        queryWrapper.eq(type != null, LabelInfo::getType, type);
        // 执行查询并返回给前端，常用于下拉选择或条件筛选场景
        List<LabelInfo> list = service.list(queryWrapper);
        return Result.ok(list);
    }

    @Operation(summary = "新增或修改标签信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdateLabel(@RequestBody LabelInfo labelInfo) {
        service.saveOrUpdate(labelInfo);
        return Result.ok();
    }

    @Operation(summary = "根据id删除标签信息")
    @DeleteMapping("deleteById")
    public Result deleteLabelById(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }
}
