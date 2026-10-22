package com.labinspection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("inspection_task")
public class InspectionTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long equipmentId;
    private Long inspectorId;
    private LocalDate planDate;
    /** PENDING待巡检 / DONE已完成 / OVERDUE已逾期 */
    private String status;
    /** NORMAL正常 / ABNORMAL异常 */
    private String result;
    private String remark;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
}
