package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AdminUserEntity;
import com.tech.repository.mapper.auth.AdminUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDao extends ServiceImpl<AdminUserMapper, AdminUserEntity> {
    public AdminUserEntity getByAccount(String account) {
        if (StringUtils.isEmpty(account)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AdminUserEntity>().eq(AdminUserEntity::getAccount, account));
    }
}
