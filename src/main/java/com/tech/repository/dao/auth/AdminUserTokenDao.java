package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AdminUserTokenEntity;
import com.tech.repository.mapper.auth.AdminUserTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AdminUserTokenDao extends ServiceImpl<AdminUserTokenMapper, AdminUserTokenEntity> {
    public AdminUserTokenEntity getByAdminUserId(Long adminUserId) {
        if (adminUserId == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AdminUserTokenEntity>().eq(AdminUserTokenEntity::getUserId, adminUserId));
    }

    public AdminUserTokenEntity getByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AdminUserTokenEntity>().eq(AdminUserTokenEntity::getToken, token));
    }
}
