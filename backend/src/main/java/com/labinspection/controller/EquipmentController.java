package com.labinspection.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labinspection.common.BizException;
import com.labinspection.common.R;
import com.labinspection.entity.Equipment;
import com.labinspection.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentMapper equipmentMapper;

    /**
     * 分页查询设备
     */
    @GetMapping("/page")
    public R<Page<Equipment>> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) Long labId) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Equipment::getName, name);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Equipment::getStatus, status);
        }
        if (labId != null) {
            wrapper.eq(Equipment::getLabId, labId);
        }
        wrapper.orderByDesc(Equipment::getCreateTime);
        Page<Equipment> page = equipmentMapper.selectPage(new Page<>(current, size), wrapper);
        return R.ok(page);
    }

    /**
     * 新增设备
     */
    @PostMapping
    public R<Void> add(@RequestBody Equipment equipment) {
        if (equipment.getLabId() == null || !StringUtils.hasText(equipment.getName())) {
            throw new BizException("实验室和设备名称不能为空");
        }
        if (!StringUtils.hasText(equipment.getStatus())) {
            equipment.setStatus("NORMAL");
        }
        equipmentMapper.insert(equipment);
        return R.ok();
    }

    /**
     * 修改设备
     */
    @PutMapping
    public R<Void> update(@RequestBody Equipment equipment) {
        if (equipment.getId() == null) {
            throw new BizException("设备ID不能为空");
        }
        equipmentMapper.updateById(equipment);
        return R.ok();
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        equipmentMapper.deleteById(id);
        return R.ok();
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public R<Equipment> detail(@PathVariable Long id) {
        return R.ok(equipmentMapper.selectById(id));
    }
}
