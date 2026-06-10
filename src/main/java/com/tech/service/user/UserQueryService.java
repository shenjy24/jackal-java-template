package com.tech.service.user;

import com.tech.common.constant.Caches;
import com.tech.repository.dao.user.UserAccountDao;
import com.tech.repository.dao.user.UserDao;
import com.tech.repository.dao.user.UserTokenDao;
import com.tech.repository.entity.user.UserEntity;
import com.tech.repository.entity.user.UserTokenEntity;
import com.tech.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserDao userDao;
    private final UserTokenDao userTokenDao;
    private final UserAccountDao userAccountDao;

    @Cacheable(Caches.CACHE_USER)
    public UserEntity getUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userDao.getById(userId);
    }

    public UserTokenEntity getUserTokenByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return userTokenDao.getUserToken(token);
    }

    public boolean isExpiredToken(UserTokenEntity userToken) {
        if (userToken == null || userToken.getExpireTime() == null) {
            return true;
        }
        return TimeUtil.currentTimestamp().compareTo(userToken.getExpireTime()) > 0;
    }
}
