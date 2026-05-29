package com.tech.service.auth;

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
    private final AuthPermissionDao authPermissionDao;
    private final AuthRolePermissionDao authRolePermissionDao;
    private final AdminUserDao adminUserDao;
    private final AdminUserTokenDao adminUserTokenDao;

    public AdminUserTokenEntity getTokenByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return adminUserTokenDao.getByToken(token);
    }

    public boolean isExpiredToken(AdminUserTokenEntity token) {
        if (token == null || token.getExpireTime() == null) {
            return true;
        }
        return TimeUtil.currentTimestamp().compareTo(token.getExpireTime()) > 0;
    }

    public Set<String> listUserPermission(Long userId, PermType permType) {
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
        roleIds = roles.stream().map(AuthRoleEntity::getRoleId).collect(Collectors.toSet());
        List<AuthRolePermissionEntity> rolePerms = authRolePermissionDao.listAuthRolePermission(roleIds);
        if (CollectionUtils.isEmpty(rolePerms)) {
            return codes;
        }
        Set<Long> permIds = rolePerms.stream().map(AuthRolePermissionEntity::getPermId).collect(Collectors.toSet());
        if (permIds.isEmpty()) {
            return codes;
        }
        List<AuthPermissionEntity> perms = authPermissionDao.listAuthPermission(permIds);
        if (permType == null) {
            return perms.stream().map(AuthPermissionEntity::getCode).collect(Collectors.toSet());
        }
        return perms.stream().filter(e -> permType.getCode().equals(e.getType()))
                .map(AuthPermissionEntity::getCode)
                .collect(Collectors.toSet());
    }
}
