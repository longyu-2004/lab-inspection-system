package com.labinspection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("lab")
public class Lab {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String building;
    private String roomNo;
    private Long managerId;
    private LocalDateTime createTime;
}
