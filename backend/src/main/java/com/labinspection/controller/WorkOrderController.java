package com.labinspection.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labinspection.common.BizException;
import com.labinspection.common.R;
import com.labinspection.entity.Equipment;
import com.labinspection.entity.WorkOrder;
import com.labinspection.mapper.EquipmentMapper;
import com.labinspection.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderMapper orderMapper;
    private final EquipmentMapper equipmentMapper;

    /**
     * 分页查询工单
     */
    @GetMapping("/page")
    public R<Page<WorkOrder>> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String status) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        wrapper.orderByDesc(WorkOrder::getCreateTime);
        return R.ok(orderMapper.selectPage(new Page<>(current, size), wrapper));
    }

    /**
     * 上报工单（手工）
     */
    @PostMapping
    public R<Void> report(@RequestBody WorkOrder order) {
        if (order.getEquipmentId() == null || order.getTitle() == null) {
            throw new BizException("设备和标题不能为空");
        }
        order.setReporterId(StpUtil.getLoginIdAsLong());
        order.setStatus("PENDING");
        orderMapper.insert(order);

        // 同步把设备状态改成异常
        Equipment equipment = equipmentMapper.selectById(order.getEquipmentId());
        if (equipment != null && !"SCRAPPED".equals(equipment.getStatus())) {
            equipment.setStatus("ABNORMAL");
            equipmentMapper.updateById(equipment);
        }
        return R.ok();
    }

    /**
     * 开始处理（管理员分派/自己接单）
     */
    @PostMapping("/{id}/process")
    public R<Void> process(@PathVariable Long id) {
        WorkOrder order = orderMapper.selectById(id);
        if (order == null) throw new BizException("工单不存在");
        if (!"PENDING".equals(order.getStatus())) throw new BizException("工单不是待处理状态");
        order.setStatus("PROCESSING");
        order.setHandleUserId(StpUtil.getLoginIdAsLong());
        orderMapper.updateById(order);
        return R.ok();
    }

    /**
     * 完成处理
     */
    @PostMapping("/{id}/finish")
    public R<Void> finish(@PathVariable Long id, @RequestBody Map<String, String> body) {
        WorkOrder order = orderMapper.selectById(id);
        if (order == null) throw new BizException("工单不存在");
        if (!"PROCESSING".equals(order.getStatus())) throw new BizException("工单不是处理中状态");
        order.setStatus("DONE");
        order.setHandleRemark(body.get("handleRemark"));
        order.setHandleTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 工单完成，把设备状态改成正常
        Equipment equipment = equipmentMapper.selectById(order.getEquipmentId());
        if (equipment != null) {
            equipment.setStatus("NORMAL");
            equipmentMapper.updateById(equipment);
        }
        return R.ok();
    }
}
