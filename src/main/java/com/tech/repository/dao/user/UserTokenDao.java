package com.tech.repository.dao.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.user.UserTokenEntity;
import com.tech.repository.mapper.user.UserTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * UserTokenManager
 *
 * @author Jonas
 * @since 2026-05-23
 */
@Service
public class UserTokenDao extends ServiceImpl<UserTokenMapper, UserTokenEntity> {
    public UserTokenEntity getUserToken(Long userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapper<UserTokenEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTokenEntity::getUserId, userId);
        return getOne(queryWrapper);
    }

    public UserTokenEntity getUserToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        LambdaQueryWrapper<UserTokenEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTokenEntity::getToken, token);
        return getOne(queryWrapper);
    }
}
