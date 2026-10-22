package com.labinspection.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labinspection.common.BizException;
import com.labinspection.common.R;
import com.labinspection.entity.Lab;
import com.labinspection.entity.SysUser;
import com.labinspection.mapper.LabMapper;
import com.labinspection.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper userMapper;
    private final LabMapper labMapper;

    @GetMapping("/page")
    public R<Page<SysUser>> page(@RequestParam(defaultValue = "1") Integer current,
                                 @RequestParam(defaultValue = "10") Integer size,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String role) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) wrapper.like(SysUser::getName, name);
        if (role != null && !role.isEmpty()) wrapper.eq(SysUser::getRole, role);
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> page = userMapper.selectPage(new Page<>(current, size), wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        return R.ok(page);
    }

    @GetMapping("/list")
    public R<List<SysUser>> list() {
        List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getId, SysUser::getName, SysUser::getRole, SysUser::getLabId));
        return R.ok(users);
    }

    @PostMapping
    public R<Void> add(@RequestBody SysUser user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getName() == null) {
            throw new BizException("账号、密码、姓名不能为空");
        }
        SysUser exist = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()));
        if (exist != null) throw new BizException("账号已存在");
        user.setStatus(1);
        userMapper.insert(user);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysUser user) {
        if (user.getId() == null) throw new BizException("ID不能为空");
        // 不允许通过此接口改密码
        user.setPassword(null);
        userMapper.updateById(user);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return R.ok();
    }

    /**
     * 实验室下拉列表
     */
    @GetMapping("/labs")
    public R<List<Lab>> labs() {
        return R.ok(labMapper.selectList(null));
    }
}
