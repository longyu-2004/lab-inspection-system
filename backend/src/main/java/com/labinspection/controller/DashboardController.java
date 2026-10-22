package com.labinspection.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.labinspection.common.R;
import com.labinspection.entity.Equipment;
import com.labinspection.entity.InspectionTask;
import com.labinspection.entity.WorkOrder;
import com.labinspection.mapper.EquipmentMapper;
import com.labinspection.mapper.InspectionTaskMapper;
import com.labinspection.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EquipmentMapper equipmentMapper;
    private final InspectionTaskMapper taskMapper;
    private final WorkOrderMapper workOrderMapper;

    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        long equipmentTotal = equipmentMapper.selectCount(null);
        long equipmentNormal = equipmentMapper.selectCount(new LambdaQueryWrapper<Equipment>()
                .eq(Equipment::getStatus, "NORMAL"));
        long equipmentAbnormal = equipmentMapper.selectCount(new LambdaQueryWrapper<Equipment>()
                .eq(Equipment::getStatus, "ABNORMAL"));

        long taskPending = taskMapper.selectCount(new LambdaQueryWrapper<InspectionTask>()
                .eq(InspectionTask::getStatus, "PENDING"));
        long taskDone = taskMapper.selectCount(new LambdaQueryWrapper<InspectionTask>()
                .eq(InspectionTask::getStatus, "DONE"));

        long orderPending = workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getStatus, "PENDING"));
        long orderProcessing = workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getStatus, "PROCESSING"));

        data.put("equipmentTotal", equipmentTotal);
        data.put("equipmentNormal", equipmentNormal);
        data.put("equipmentAbnormal", equipmentAbnormal);
        data.put("taskPending", taskPending);
        data.put("taskDone", taskDone);
        data.put("orderPending", orderPending);
        data.put("orderProcessing", orderProcessing);
        return R.ok(data);
    }
}
