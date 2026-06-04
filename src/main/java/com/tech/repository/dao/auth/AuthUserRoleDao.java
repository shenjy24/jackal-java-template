package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthUserRoleEntity;
import com.tech.repository.mapper.auth.AuthUserRoleMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthUserRoleDao extends ServiceImpl<AuthUserRoleMapper, AuthUserRoleEntity> {
    public List<AuthUserRoleEntity> listAuthUserRole(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<AuthUserRoleEntity>().eq(AuthUserRoleEntity::getUserId, userId));
    }

    public void bindUserRole(Long userId, List<Long> roleIds) {
        remove(new LambdaUpdateWrapper<AuthUserRoleEntity>().eq(AuthUserRoleEntity::getUserId, userId));
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<AuthUserRoleEntity> userRoles = roleIds.stream().distinct().map(roleId -> {
            AuthUserRoleEntity entity = new AuthUserRoleEntity();
            entity.setUserId(userId);
            entity.setRoleId(roleId);
            return entity;
        }).collect(Collectors.toList());
        saveBatch(userRoles);
    }

    public void removeByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        remove(new LambdaUpdateWrapper<AuthUserRoleEntity>().eq(AuthUserRoleEntity::getUserId, userId));
    }

    public void removeByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        remove(new LambdaUpdateWrapper<AuthUserRoleEntity>().eq(AuthUserRoleEntity::getRoleId, roleId));
    }
}
