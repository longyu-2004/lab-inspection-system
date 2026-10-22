package com.labinspection.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labinspection.common.BizException;
import com.labinspection.common.R;
import com.labinspection.entity.Equipment;
import com.labinspection.entity.InspectionTask;
import com.labinspection.entity.WorkOrder;
import com.labinspection.mapper.EquipmentMapper;
import com.labinspection.mapper.InspectionTaskMapper;
import com.labinspection.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionTaskMapper taskMapper;
    private final EquipmentMapper equipmentMapper;
    private final WorkOrderMapper workOrderMapper;

    /**
     * 分页查询巡检任务
     */
    @GetMapping("/page")
    public R<Page<InspectionTask>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String status) {
        LambdaQueryWrapper<InspectionTask> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(InspectionTask::getStatus, status);
        }
        wrapper.orderByDesc(InspectionTask::getCreateTime);
        return R.ok(taskMapper.selectPage(new Page<>(current, size), wrapper));
    }

    /**
     * 创建巡检任务（管理员/实验室管理员用）
     */
    @PostMapping
    public R<Void> add(@RequestBody InspectionTask task) {
        if (task.getEquipmentId() == null || task.getInspectorId() == null || task.getPlanDate() == null) {
            throw new BizException("设备、巡检员、计划日期不能为空");
        }
        task.setStatus("PENDING");
        taskMapper.insert(task);
        return R.ok();
    }

    /**
     * 巡检员执行巡检
     * 如果结果是异常，自动创建一条工单
     */
    @PostMapping("/{id}/execute")
    public R<Void> execute(@PathVariable Long id, @RequestBody Map<String, String> body) {
        InspectionTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BizException("任务不存在");
        }
        if (!"PENDING".equals(task.getStatus())) {
            throw new BizException("任务不是待巡检状态，不能重复执行");
        }
        String result = body.get("result");
        String remark = body.get("remark");
        task.setResult(result);
        task.setRemark(remark);
        task.setStatus("DONE");
        task.setFinishTime(LocalDateTime.now());
        taskMapper.updateById(task);

        // 如果结果异常，更新设备状态并自动建工单
        if ("ABNORMAL".equals(result)) {
            Equipment equipment = equipmentMapper.selectById(task.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus("ABNORMAL");
                equipmentMapper.updateById(equipment);

                WorkOrder order = new WorkOrder();
                order.setEquipmentId(equipment.getId());
                order.setReporterId(task.getInspectorId());
                order.setTitle("设备异常巡检上报：" + equipment.getName());
                order.setDescription(remark == null ? "巡检发现异常" : remark);
                order.setStatus("PENDING");
                workOrderMapper.insert(order);
            }
        }
        return R.ok();
    }
}
