-- =============================================
-- 高校实验室设备巡检管理系统 - 数据库初始化脚本
-- MySQL 8.0+
-- =============================================

DROP DATABASE IF EXISTS lab_inspection;
CREATE DATABASE lab_inspection DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lab_inspection;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录账号',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    name        VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL COMMENT '角色：ADMIN系统管理员 / MANAGER实验室管理员 / INSPECTOR巡检员',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    lab_id      BIGINT       DEFAULT NULL COMMENT '所属实验室ID',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 实验室表
-- ----------------------------
DROP TABLE IF EXISTS lab;
CREATE TABLE lab (
    id          BIGINT      PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100) NOT NULL COMMENT '实验室名称',
    building    VARCHAR(50)  NOT NULL COMMENT '楼栋',
    room_no     VARCHAR(50)  NOT NULL COMMENT '房间号',
    manager_id  BIGINT       DEFAULT NULL COMMENT '管理员用户ID',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室表';

-- ----------------------------
-- 3. 设备表
-- ----------------------------
DROP TABLE IF EXISTS equipment;
CREATE TABLE equipment (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    lab_id      BIGINT       NOT NULL COMMENT '所属实验室ID',
    name        VARCHAR(100) NOT NULL COMMENT '设备名称',
    model       VARCHAR(100) DEFAULT NULL COMMENT '设备型号',
    asset_no    VARCHAR(50)  NOT NULL UNIQUE COMMENT '资产编号',
    status      VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL正常 / ABNORMAL异常 / REPAIRING维修中 / SCRAPPED报废',
    install_date DATE         DEFAULT NULL COMMENT '安装日期',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- ----------------------------
-- 4. 巡检任务表
-- ----------------------------
DROP TABLE IF EXISTS inspection_task;
CREATE TABLE inspection_task (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    equipment_id  BIGINT       NOT NULL COMMENT '设备ID',
    inspector_id  BIGINT       NOT NULL COMMENT '巡检员用户ID',
    plan_date     DATE         NOT NULL COMMENT '计划巡检日期',
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING待巡检 / DONE已完成 / OVERDUE已逾期',
    result        VARCHAR(20)  DEFAULT NULL COMMENT '巡检结果：NORMAL正常 / ABNORMAL异常',
    remark        VARCHAR(500) DEFAULT NULL COMMENT '巡检备注',
    finish_time   DATETIME     DEFAULT NULL COMMENT '实际完成时间',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检任务表';

-- ----------------------------
-- 5. 异常工单表
-- ----------------------------
DROP TABLE IF EXISTS work_order;
CREATE TABLE work_order (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    equipment_id   BIGINT       NOT NULL COMMENT '设备ID',
    reporter_id    BIGINT       NOT NULL COMMENT '上报人ID',
    title          VARCHAR(200) NOT NULL COMMENT '工单标题',
    description    VARCHAR(1000) NOT NULL COMMENT '问题描述',
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING待处理 / PROCESSING处理中 / DONE已完成',
    handle_user_id BIGINT       DEFAULT NULL COMMENT '处理人ID',
    handle_remark  VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
    handle_time    DATETIME     DEFAULT NULL COMMENT '处理时间',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常工单表';

-- =============================================
-- 测试数据
-- =============================================

-- 用户（演示用明文密码，生产环境必须加密存储）
-- 账号分别为：admin / zhangsan / lisi / wangwu，统一密码：123456
INSERT INTO sys_user (username, password, name, role, phone, lab_id) VALUES
('admin',    '123456', '系统管理员', 'ADMIN',     '13800000001', NULL),
('zhangsan', '123456', '张三',       'MANAGER',   '13800000002', 1),
('lisi',     '123456', '李四',       'INSPECTOR', '13800000003', 1),
('wangwu',   '123456', '王五',       'INSPECTOR', '13800000004', 2);

-- 实验室
INSERT INTO lab (name, building, room_no, manager_id) VALUES
('嵌入式系统实验室', '工学楼A', 'A301', 2),
('软件工程实验室',   '工学楼B', 'B205', 3);

-- 设备
INSERT INTO equipment (lab_id, name, model, asset_no, status, install_date, remark) VALUES
(1, '示波器',     'DS1054Z',  'SB2024001', 'NORMAL',     '2023-09-01', '正常使用'),
(1, '函数信号发生器', 'DG1022U', 'SB2024002', 'NORMAL',     '2023-09-01', NULL),
(1, '直流电源',   'DP832',    'SB2024003', 'ABNORMAL',   '2023-09-01', '输出电压不稳'),
(2, '服务器机柜', 'PowerEdge R750', 'SB2024010', 'NORMAL', '2024-03-15', NULL),
(2, '学生电脑',   'OptiPlex 7090',  'SB2024011', 'REPAIRING', '2024-03-15', '键盘失灵报修中');

-- 巡检任务
INSERT INTO inspection_task (equipment_id, inspector_id, plan_date, status, result, remark, finish_time) VALUES
(1, 3, '2026-10-20', 'DONE',    'NORMAL', '外观完好，测试正常', '2026-10-20 10:30:00'),
(2, 3, '2026-10-20', 'DONE',    'NORMAL', '正常',              '2026-10-20 11:00:00'),
(3, 3, '2026-10-22', 'PENDING', NULL,    NULL, NULL),
(4, 4, '2026-10-22', 'PENDING', NULL,    NULL, NULL),
(5, 4, '2026-10-21', 'DONE',    'ABNORMAL', '键盘失灵，已上报工单', '2026-10-21 15:00:00');

-- 工单
INSERT INTO work_order (equipment_id, reporter_id, title, description, status, handle_user_id, handle_remark, handle_time) VALUES
(3, 3, '直流电源输出电压不稳', 'DP832开机后CH1输出电压显示波动，无法稳定在设定值', 'PENDING', NULL, NULL, NULL),
(5, 4, '学生电脑键盘失灵',     'OptiPlex 7090编号SB2024011，部分按键无响应',       'PROCESSING', 2, '已联系售后更换键盘', '2026-10-21 16:00:00');
