package com.tech.repository.dao.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.user.UserAccountEntity;
import com.tech.repository.mapper.user.UserAccountMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * UserAccountManager
 *
 * @author Jonas
 * @since 2026-05-23
 */
@Service
public class UserAccountDao extends ServiceImpl<UserAccountMapper, UserAccountEntity> {
    public UserAccountEntity getUserAccount(String account) {
        if (StringUtils.isEmpty(account)) {
            return null;
        }
        LambdaQueryWrapper<UserAccountEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAccountEntity::getAccount, account);
        return getOne(queryWrapper);
    }
}
