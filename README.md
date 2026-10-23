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


# 七、常见启动问题

**Q：后端启动报"Communications link failure"**
A：MySQL 没启动，或者 application.yml 里的密码不对。

**Q：前端 npm install 很慢**
A：换淘宝源：`npm config set registry https://registry.npmmirror.com`

**Q：前端登录后页面空白**
A：打开浏览器 F12 看 Network，看 /api/auth/login 请求是不是 404。如果是，说明后端没起；如果是 CORS 报错，确认后端端口是 8080。
