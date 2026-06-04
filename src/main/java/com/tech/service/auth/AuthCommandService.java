package com.tech.service.auth;

import com.tech.common.enums.ErrorCode;
import com.tech.config.response.bean.BizException;
import com.tech.repository.dao.auth.AuthUserDao;
import com.tech.repository.dao.auth.AuthUserTokenDao;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.entity.auth.AuthUserTokenEntity;
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

    private final AuthUserDao authUserDao;
    private final AuthUserTokenDao authUserTokenDao;

    public AuthUserEntity loginByAccount(String account, String password) {
        AuthUserEntity adminUser = authUserDao.getByAccount(account);
        if (adminUser == null || CryptoUtil.notMatches(password, adminUser.getPassword())) {
            throw new BizException(ErrorCode.USER_ERROR4);
        }
        AuthUserTokenEntity token = saveOrUpdateToken(adminUser.getId());
        CookieUtil.setCookie(token.getToken());
        return adminUser;
    }

    private AuthUserTokenEntity saveOrUpdateToken(Long adminUserId) {
        if (adminUserId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        AuthUserTokenEntity token = authUserTokenDao.getByAdminUserId(adminUserId);
        String tokenValue = IdUtil.uuid();
        Timestamp expireTime = TimeUtil.tokenExpireTime();
        if (token == null) {
            token = new AuthUserTokenEntity(adminUserId, tokenValue, expireTime);
            authUserTokenDao.save(token);
        } else {
            token.setToken(tokenValue).setExpireTime(expireTime);
            authUserTokenDao.updateById(token);
        }
        return token;
    }

    public void updateToken(AuthUserTokenEntity token) {
        if (token == null || token.getId() == null) {
            return;
        }
        authUserTokenDao.updateById(token);
    }
}
