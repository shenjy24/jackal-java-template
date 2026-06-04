package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthRolePermEntity;
import com.tech.repository.mapper.auth.AuthRolePermMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class AuthRolePermDao extends ServiceImpl<AuthRolePermMapper, AuthRolePermEntity> {
    public List<AuthRolePermEntity> listAuthRolePermission(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<AuthRolePermEntity>().in(AuthRolePermEntity::getRoleId, roleIds));
    }
}
