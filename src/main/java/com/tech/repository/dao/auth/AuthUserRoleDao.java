package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthUserRoleEntity;
import com.tech.repository.mapper.auth.AuthUserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AuthUserRoleDao extends ServiceImpl<AuthUserRoleMapper, AuthUserRoleEntity> {
    public List<AuthUserRoleEntity> listAuthUserRole(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<AuthUserRoleEntity>().eq(AuthUserRoleEntity::getUserId, userId));
    }
}
