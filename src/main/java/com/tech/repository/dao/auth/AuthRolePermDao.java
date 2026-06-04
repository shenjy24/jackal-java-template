package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthRolePermEntity;
import com.tech.repository.mapper.auth.AuthRolePermMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthRolePermDao extends ServiceImpl<AuthRolePermMapper, AuthRolePermEntity> {
    public List<AuthRolePermEntity> listAuthRolePermission(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<AuthRolePermEntity>().in(AuthRolePermEntity::getRoleId, roleIds));
    }

    public List<AuthRolePermEntity> listAuthRolePerm(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<AuthRolePermEntity>().eq(AuthRolePermEntity::getRoleId, roleId));
    }

    public void bindRolePerm(Long roleId, List<Long> permIds) {
        LambdaQueryWrapper<AuthRolePermEntity> wrapper = new LambdaQueryWrapper<AuthRolePermEntity>().eq(AuthRolePermEntity::getRoleId, roleId);
        this.remove(wrapper);
        if (CollectionUtils.isEmpty(permIds)) {
            return;
        }
        List<AuthRolePermEntity> rolePerms = permIds.stream().distinct().map(permId -> {
            AuthRolePermEntity entity = new AuthRolePermEntity();
            entity.setRoleId(roleId);
            entity.setPermId(permId);
            return entity;
        }).collect(Collectors.toList());
        saveBatch(rolePerms);
    }

    public void removeByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        remove(new LambdaUpdateWrapper<AuthRolePermEntity>().eq(AuthRolePermEntity::getRoleId, roleId));
    }

    public void removeByPermId(Long permId) {
        if (permId == null) {
            return;
        }
        remove(new LambdaUpdateWrapper<AuthRolePermEntity>().eq(AuthRolePermEntity::getPermId, permId));
    }
}
