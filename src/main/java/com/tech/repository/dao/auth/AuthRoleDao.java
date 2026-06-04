package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.mapper.auth.AuthRoleMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class AuthRoleDao extends ServiceImpl<AuthRoleMapper, AuthRoleEntity> {
    public List<AuthRoleEntity> listAuthRole(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<AuthRoleEntity>().in(AuthRoleEntity::getId, roleIds));
    }

    public IPage<AuthRoleEntity> queryAuthRole(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AuthRoleEntity> wrapper = new LambdaQueryWrapper<AuthRoleEntity>()
                .like(StringUtils.isNotBlank(name), AuthRoleEntity::getName, name)
                .orderByDesc(AuthRoleEntity::getId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
