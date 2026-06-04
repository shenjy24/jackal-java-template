package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.mapper.auth.AuthUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDao extends ServiceImpl<AuthUserMapper, AuthUserEntity> {
    public AuthUserEntity getByAccount(String account) {
        if (StringUtils.isEmpty(account)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AuthUserEntity>().eq(AuthUserEntity::getAccount, account));
    }

    public IPage<AuthUserEntity> queryAuthUser(String account, String nickname, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AuthUserEntity> wrapper = new LambdaQueryWrapper<AuthUserEntity>()
                .like(StringUtils.isNotBlank(account), AuthUserEntity::getAccount, account)
                .like(StringUtils.isNotBlank(nickname), AuthUserEntity::getNickname, nickname)
                .orderByDesc(AuthUserEntity::getId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
