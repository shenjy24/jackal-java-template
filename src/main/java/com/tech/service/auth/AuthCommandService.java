package com.tech.service.auth;

import com.tech.common.enums.ErrorCode;
import com.tech.config.response.bean.BizException;
import com.tech.repository.dao.auth.AdminUserDao;
import com.tech.repository.dao.auth.AdminUserTokenDao;
import com.tech.repository.entity.auth.AdminUserEntity;
import com.tech.repository.entity.auth.AdminUserTokenEntity;
import com.tech.util.CookieUtil;
import com.tech.util.CryptoUtil;
import com.tech.util.IdUtil;
import com.tech.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final AdminUserDao adminUserDao;
    private final AdminUserTokenDao adminUserTokenDao;

    public AdminUserEntity loginByAccount(String account, String password) {
        AdminUserEntity adminUser = adminUserDao.getByAccount(account);
        if (adminUser == null || CryptoUtil.notMatches(password, adminUser.getPassword())) {
            throw new BizException(ErrorCode.USER_ERROR4);
        }
        AdminUserTokenEntity token = saveOrUpdateToken(adminUser.getUserId());
        CookieUtil.setCookie(token.getToken());
        return adminUser;
    }

    private AdminUserTokenEntity saveOrUpdateToken(Long adminUserId) {
        if (adminUserId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        AdminUserTokenEntity token = adminUserTokenDao.getByAdminUserId(adminUserId);
        String tokenValue = IdUtil.uuid();
        Timestamp expireTime = TimeUtil.tokenExpireTime();
        if (token == null) {
            token = new AdminUserTokenEntity(adminUserId, tokenValue, expireTime);
            adminUserTokenDao.save(token);
        } else {
            token.setToken(tokenValue).setExpireTime(expireTime);
            adminUserTokenDao.updateById(token);
        }
        return token;
    }

    public void updateToken(AdminUserTokenEntity token) {
        if (token == null || token.getTokenId() == null) {
            return;
        }
        adminUserTokenDao.updateById(token);
    }
}
