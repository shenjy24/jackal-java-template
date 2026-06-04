package com.tech.service.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tech.common.enums.auth.PermType;
import com.tech.repository.dao.auth.*;
import com.tech.repository.entity.auth.*;
import com.tech.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthQueryService {

    private final AuthRoleDao authRoleDao;
    private final AuthUserRoleDao authUserRoleDao;
    private final AuthPermDao authPermDao;
    private final AuthRolePermDao authRolePermDao;
    private final AuthUserDao authUserDao;
    private final AuthUserTokenDao authUserTokenDao;

    public AuthUserTokenEntity getTokenByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return authUserTokenDao.getByToken(token);
    }

    public boolean isExpiredToken(AuthUserTokenEntity token) {
        if (token == null || token.getExpireTime() == null) {
            return true;
        }
        return TimeUtil.currentTimestamp().compareTo(token.getExpireTime()) > 0;
    }

    public Set<String> listUserPerm(Long userId, PermType permType) {
        Set<String> codes = new HashSet<>();
        if (userId == null) {
            return codes;
        }
        List<AuthUserRoleEntity> userRoles = authUserRoleDao.listAuthUserRole(userId);
        if (CollectionUtils.isEmpty(userRoles)) {
            return codes;
        }
        Set<Long> roleIds = userRoles.stream().map(AuthUserRoleEntity::getRoleId).collect(Collectors.toSet());
        List<AuthRoleEntity> roles = authRoleDao.listAuthRole(roleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return codes;
        }
        roleIds = roles.stream().map(AuthRoleEntity::getId).collect(Collectors.toSet());
        List<AuthRolePermEntity> rolePerms = authRolePermDao.listAuthRolePermission(roleIds);
        if (CollectionUtils.isEmpty(rolePerms)) {
            return codes;
        }
        Set<Long> permIds = rolePerms.stream().map(AuthRolePermEntity::getPermId).collect(Collectors.toSet());
        if (permIds.isEmpty()) {
            return codes;
        }
        List<AuthPermEntity> perms = authPermDao.listAuthPermission(permIds);
        if (permType == null) {
            return perms.stream().map(AuthPermEntity::getCode).collect(Collectors.toSet());
        }
        return perms.stream().filter(e -> permType.getCode().equals(e.getType()))
                .map(AuthPermEntity::getCode)
                .collect(Collectors.toSet());
    }

    public AuthUserEntity getAuthUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return authUserDao.getById(userId);
    }

    public IPage<AuthUserEntity> queryAuthUser(String account, String nickname, Integer pageNum, Integer pageSize) {
        return authUserDao.queryAuthUser(account, nickname, pageNum, pageSize);
    }

    public IPage<AuthRoleEntity> queryAuthRole(String code, String name, Integer pageNum, Integer pageSize) {
        return authRoleDao.queryAuthRole(code, name, pageNum, pageSize);
    }

    public IPage<AuthPermEntity> queryAuthPerm(String code, String name, Integer type, Integer pageNum, Integer pageSize) {
        return authPermDao.queryAuthPerm(code, name, type, pageNum, pageSize);
    }
}
