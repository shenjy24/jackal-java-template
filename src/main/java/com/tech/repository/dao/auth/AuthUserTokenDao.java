package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthUserTokenEntity;
import com.tech.repository.mapper.auth.AuthUserTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthUserTokenDao extends ServiceImpl<AuthUserTokenMapper, AuthUserTokenEntity> {
    public AuthUserTokenEntity getAuthUserToken(Long adminUserId) {
        if (adminUserId == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AuthUserTokenEntity>().eq(AuthUserTokenEntity::getUserId, adminUserId));
    }

    public AuthUserTokenEntity getAuthUserToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AuthUserTokenEntity>().eq(AuthUserTokenEntity::getToken, token));
    }
}
