package com.labinspection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_order")
public class WorkOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long equipmentId;
    private Long reporterId;
    private String title;
    private String description;
    /** PENDING待处理 / PROCESSING处理中 / DONE已完成 */
    private String status;
    private Long handleUserId;
    private String handleRemark;
    private LocalDateTime handleTime;
    private LocalDateTime createTime;
}
