package com.labinspection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long labId;
    private String name;
    private String model;
    private String assetNo;
    /** NORMAL正常 / ABNORMAL异常 / REPAIRING维修中 / SCRAPPED报废 */
    private String status;
    private LocalDate installDate;
    private String remark;
    private LocalDateTime createTime;
}
