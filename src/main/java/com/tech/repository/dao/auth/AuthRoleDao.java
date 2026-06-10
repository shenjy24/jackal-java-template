package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.mapper.auth.AuthRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthRoleDao extends ServiceImpl<AuthRoleMapper, AuthRoleEntity> {

    public IPage<AuthRoleEntity> queryAuthRole(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AuthRoleEntity> wrapper = new LambdaQueryWrapper<AuthRoleEntity>()
                .like(StringUtils.isNotBlank(name), AuthRoleEntity::getName, name)
                .orderByDesc(AuthRoleEntity::getId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
