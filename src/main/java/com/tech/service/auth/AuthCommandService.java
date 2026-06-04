package com.tech.service.auth;

import com.tech.common.constant.Constants;
import com.tech.common.enums.ErrorCode;
import com.tech.config.response.bean.BizException;
import com.tech.repository.dao.auth.*;
import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.entity.auth.AuthUserTokenEntity;
import com.tech.util.CookieUtil;
import com.tech.util.CryptoUtil;
import com.tech.util.IdUtil;
import com.tech.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final AuthUserDao authUserDao;
    private final AuthUserTokenDao authUserTokenDao;
    private final AuthRoleDao authRoleDao;
    private final AuthPermDao authPermDao;
    private final AuthUserRoleDao authUserRoleDao;
    private final AuthRolePermDao authRolePermDao;

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

    public AuthUserEntity saveAuthUser(String nickname, String avatar, String account) {
        if (StringUtils.isBlank(account)) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        AuthUserEntity existUser = authUserDao.getByAccount(account);
        if (existUser != null) {
            throw new BizException(ErrorCode.USER_ERROR5);
        }
        AuthUserEntity user = new AuthUserEntity()
                .setNickname(nickname)
                .setAvatar(avatar)
                .setAccount(account)
                .setPassword(CryptoUtil.encode(Constants.DEFAULT_PASSWORD));
        authUserDao.save(user);
        return user;
    }

    public AuthUserEntity updateAuthUser(Long id, String nickname, String avatar, String account) {
        AuthUserEntity user = authUserDao.getById(id);
        if (user == null) {
            throw new BizException(ErrorCode.USER_ERROR3);
        }
        if (StringUtils.isNotBlank(account) && !account.equals(user.getAccount())) {
            AuthUserEntity existUser = authUserDao.getByAccount(account);
            if (existUser != null) {
                throw new BizException(ErrorCode.USER_ERROR5);
            }
            user.setAccount(account);
        }
        user.setNickname(nickname);
        user.setAvatar(avatar);
        authUserDao.updateById(user);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthUser(Long userId) {
        authUserDao.removeById(userId);
        authUserRoleDao.removeByUserId(userId);
    }

    public void updateAuthUserPassword(Long userId, String oldPassword, String newPassword) {
        AuthUserEntity user = authUserDao.getById(userId);
        if (user == null || CryptoUtil.notMatches(oldPassword, user.getPassword())) {
            throw new BizException(ErrorCode.USER_ERROR4);
        }
        user.setPassword(CryptoUtil.encode(newPassword));
        authUserDao.updateById(user);
    }

    public void resetAuthUserPassword(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        AuthUserEntity user = authUserDao.getById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_ERROR3);
        }
        user.setPassword(CryptoUtil.encode(Constants.DEFAULT_PASSWORD));
        authUserDao.updateById(user);
    }

    public AuthRoleEntity saveAuthRole(String code, String name, String remark) {
        AuthRoleEntity role = new AuthRoleEntity()
                .setCode(code)
                .setName(name)
                .setRemark(remark);
        authRoleDao.save(role);
        return role;
    }

    public AuthRoleEntity updateAuthRole(Long id, String code, String name, String remark) {
        AuthRoleEntity role = authRoleDao.getById(id);
        if (role == null) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        role.setCode(code);
        role.setName(name);
        role.setRemark(remark);
        authRoleDao.updateById(role);
        return role;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthRole(Long roleId) {
        authRoleDao.removeById(roleId);
        authUserRoleDao.removeByRoleId(roleId);
        authRolePermDao.removeByRoleId(roleId);
    }

    public AuthPermEntity saveAuthPerm(Long parentId, String code, String name, Integer type, String remark) {
        AuthPermEntity perm = new AuthPermEntity()
                .setParentId(parentId)
                .setCode(code)
                .setName(name)
                .setType(type)
                .setRemark(remark);
        authPermDao.save(perm);
        return perm;
    }

    public AuthPermEntity updateAuthPerm(Long id, Long parentId, String code, String name, Integer type, String remark) {
        AuthPermEntity perm = authPermDao.getById(id);
        if (perm == null) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        perm.setParentId(parentId);
        perm.setCode(code);
        perm.setName(name);
        perm.setType(type);
        perm.setRemark(remark);
        authPermDao.updateById(perm);
        return perm;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthPerm(Long permId) {
        authPermDao.removeById(permId);
        authRolePermDao.removeByPermId(permId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindUserRole(Long userId, List<Long> roleIds) {
        if (authUserDao.getById(userId) == null) {
            throw new BizException(ErrorCode.USER_ERROR3);
        }
        authUserRoleDao.bindUserRole(userId, roleIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindRolePerm(Long roleId, List<Long> permIds) {
        if (authRoleDao.getById(roleId) == null) {
            throw new BizException(ErrorCode.PARAM_ERROR);
        }
        authRolePermDao.bindRolePerm(roleId, permIds);
    }
}
