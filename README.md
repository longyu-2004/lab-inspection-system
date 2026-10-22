# 高校实验室设备巡检管理系统

## 一、项目介绍

面向高校实验室场景，解决设备台账分散、巡检无记录、异常维修无闭环的问题。
覆盖 **设备管理 → 巡检任务派发 → 巡检员现场打卡 → 异常自动生成工单 → 维修闭环** 全流程。

## 二、技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 17、Spring Boot 3.2、MyBatis-Plus、MySQL 8、Sa-Token |
| 前端 | Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios |
| 鉴权 | Sa-Token（Token模式） |

## 三、环境要求（启动前确认）

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

## 四、启动步骤

### 1. 数据库
```bash
# 用 MySQL 客户端执行
mysql -u root -p < sql/init.sql
```
脚本会自动建库 `lab_inspection`、建表、插入测试数据。

### 2. 后端
```bash
cd backend
# 修改 src/main/resources/application.yml 第 11 行的数据库密码为你本地的MySQL密码
mvn spring-boot:run
```
启动成功后端口为 **8080**。

### 3. 前端
```bash
cd frontend
npm install
npm run dev
```
启动成功后浏览器访问 **http://localhost:5173**

## 五、默认账号

| 账号 | 密码 | 角色 |
|---|---|---|
| admin | 123456 | 系统管理员 |
| zhangsan | 123456 | 实验室管理员 |
| lisi | 123456 | 巡检员 |
| wangwu | 123456 | 巡检员 |

## 六、核心业务闭环

1. 管理员在「设备管理」登记实验室所有设备
2. 管理员在「巡检任务」给巡检员派发任务（计划日期）
3. 巡检员在「巡检任务」页面看到待办，点「执行巡检」填写结果
4. **如果结果是"异常"，系统自动把设备状态改成异常，并自动生成一条待处理工单**
5. 管理员/处理人在「异常工单」接单 → 维修 → 填处理备注完成
6. **工单完成后，设备状态自动恢复为正常**

## 七、生产环境需改进的地方（面试可能会问）

- 密码目前是明文存储，生产环境必须用 BCryptPasswordEncoder 加密
- 目前 Sa-Token 用内存存储 Token，重启后登录态丢失，生产环境要换成 Redis
- 没有操作日志（可引入AOP记录）
- 没有文件上传（巡检拍照上传可扩展）

---

# 八、简历话术（直接复制用）

## 项目名称
高校实验室设备巡检管理系统

## 项目时间
2026.03 - 2026.05（按你实际起止时间改）

## 项目描述
面向高校实验室设备管理场景，独立设计并实现的前后端分离 Web 系统，覆盖设备台账、巡检任务派发、巡检员现场打卡、异常工单流转的完整业务闭环，支持系统管理员、实验室管理员、巡检员三种角色。

## 技术栈
Java、Spring Boot 3、MyBatis-Plus、MySQL、Sa-Token、Vue 3、Element Plus、Vite

## 主要职责 / 工作内容
1. 独立完成数据库设计，包含用户、实验室、设备、巡检任务、异常工单 5 张核心表，使用 MyBatis-Plus 实现通用 CRUD 与分页查询
2. 基于 Sa-Token 实现 Token 登录鉴权，统一处理跨域、全局异常返回（R 包装类）
3. 实现业务联动：巡检员上报异常时自动更新设备状态并生成待处理工单；工单完成后自动将设备状态恢复为正常
4. 基于 Vue 3 + Element Plus 实现响应式后台界面，封装 Axios 拦截器统一处理 Token 注入与 401 跳转

## 项目亮点（面试能讲 2 分钟）
- **不是简单CRUD系统**：巡检结果与设备状态、工单状态之间有自动联动逻辑，形成"发现异常→上报→维修→恢复"的业务闭环
- **独立完成前后端**：从数据库设计、接口开发到前端页面全部独立实现

---

# 九、面试高频问题准备

**Q1：为什么用 Sa-Token 不用 Spring Security？**
A：Sa-Token 是国产轻量鉴权框架，API 简洁（StpUtil.login、StpUtil.checkRole），对中小型后台系统足够用；Spring Security 配置复杂、学习曲线陡，对本项目这种规模属于过度设计。

**Q2：MyBatis-Plus 相比 MyBatis 有什么好处？**
A：内置通用 Mapper（BaseMapper），简单单表 CRUD 不用写 XML；内置分页插件（PaginationInnerInterceptor），分页一行代码搞定；支持 Lambda 条件构造器，字段名编译期检查，改字段名不容易漏改。

**Q3：Token 是怎么存的？怎么校验的？**
A：登录成功后调用 StpUtil.login(userId)，Sa-Token 生成 Token 返回给前端；前端把 Token 存在 localStorage，每次请求通过 Axios 拦截器塞到 Authorization 请求头；后端 Sa-Token 拦截器校验 Token 有效性，未登录抛 NotLoginException，被全局异常处理器统一返回 401。

**Q4：这个项目最有技术含量的地方是什么？**
A：业务状态机的联动。巡检任务状态（PENDING/DONE）、设备状态（NORMAL/ABNORMAL）、工单状态（PENDING/PROCESSING/DONE）三者不是独立的——巡检异常时设备自动变异常+自动建工单，工单关闭后设备自动恢复正常。这种跨表事务级联动是项目里最能体现业务理解的部分。

**Q5：数据库表设计为什么这样分？**
A：按领域分。用户、实验室、设备是基础数据；巡检任务是"计划与执行"的中间表；工单是"异常处理"的流程表。每张表职责单一，比如设备表不存巡检结果（放到任务表），任务表不存维修记录（放工单表），符合第一范式。

**Q6：如果要扩展一个"巡检结果拍照上传"功能，你怎么做？**
A：后端加一个文件上传接口（Spring 自带 MultipartFile），图片存到本地或 OSS，返回 URL 存到巡检任务的 remark 字段或新建一个 inspection_attachment 表；前端用 Element Plus 的 el-upload 组件。要注意文件大小限制、类型校验、路径隔离。

**Q7：你这个项目部署过吗？**
A：诚实回答。如果没部署过，可以说"准备部署到云服务器，用 Nginx 反向代理前端静态资源 + 转发后端接口"。**强烈建议你真的部署一次**——买个 99 块一年的阿里云学生机，能在简历上写"已部署上线，访问地址 xxx"，直接超过 80% 的同龄人。

---

# 十、常见启动问题

**Q：后端启动报"Communications link failure"**
A：MySQL 没启动，或者 application.yml 里的密码不对。

**Q：前端 npm install 很慢**
A：换淘宝源：`npm config set registry https://registry.npmmirror.com`

**Q：前端登录后页面空白**
A：打开浏览器 F12 看 Network，看 /api/auth/login 请求是不是 404。如果是，说明后端没起；如果是 CORS 报错，确认后端端口是 8080。
