package com.tech.service.user;

import com.tech.common.enums.ErrorCode;
import com.tech.config.response.bean.BizException;
import com.tech.repository.dao.user.UserAccountDao;
import com.tech.repository.dao.user.UserDao;
import com.tech.repository.dao.user.UserTokenDao;
import com.tech.repository.entity.user.UserAccountEntity;
import com.tech.repository.entity.user.UserEntity;
import com.tech.repository.entity.user.UserTokenEntity;
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
public class UserCommandService {

    private final UserDao userDao;
    private final UserTokenDao userTokenDao;
    private final UserAccountDao userAccountDao;

    public UserEntity loginByAccount(String account, String password) {
        UserAccountEntity userAccount = userAccountDao.getUserAccount(account);
        if (userAccount == null) {
            throw new BizException(ErrorCode.USER_ERROR4);
        }
        if (CryptoUtil.notMatches(password, userAccount.getPassword())) {
            throw new BizException(ErrorCode.USER_ERROR4);
        }
        UserEntity user = userDao.getById(userAccount.getUserId());
        if (user == null) {
            throw new BizException(ErrorCode.USER_ERROR4);
        }
        UserTokenEntity userToken = saveOrUpdateUserToken(userAccount.getUserId());
        CookieUtil.setCookie(userToken.getToken());
        return user;
    }

    private UserTokenEntity saveOrUpdateUserToken(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        UserTokenEntity userToken = userTokenDao.getUserToken(userId);
        String token = IdUtil.uuid();
        Timestamp expireTime = TimeUtil.tokenExpireTime();
        if (null == userToken) {
            userToken = new UserTokenEntity().setUserId(userId).setToken(token).setExpireTime(expireTime);
            userTokenDao.save(userToken);
        } else {
            userToken.setToken(token).setExpireTime(expireTime);
            userTokenDao.updateById(userToken);
        }
        return userToken;
    }

    public void updateUserToken(UserTokenEntity userToken) {
        if (userToken == null || userToken.getId() == null) {
            return;
        }
        userTokenDao.updateById(userToken);
    }
}
