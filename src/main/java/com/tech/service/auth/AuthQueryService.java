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

import java.util.Collections;
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
        return authUserTokenDao.getAuthUserToken(token);
    }

    public boolean isExpiredToken(AuthUserTokenEntity token) {
        if (token == null || token.getExpireTime() == null) {
            return true;
        }
        return TimeUtil.currentTimestamp().compareTo(token.getExpireTime()) > 0;
    }

    public Set<String> listUserPermCode(Long userId, PermType permType) {
        return listUserPerm(userId, permType).stream()
                .map(AuthPermEntity::getCode)
                .collect(Collectors.toSet());
    }

    public List<AuthPermEntity> listUserPerm(Long userId, PermType permType) {
        if (userId == null) {
            return Collections.emptyList();
        }
        List<AuthUserRoleEntity> userRoles = authUserRoleDao.listAuthUserRole(userId);
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        Set<Long> roleIds = userRoles.stream().map(AuthUserRoleEntity::getRoleId).collect(Collectors.toSet());
        List<AuthRoleEntity> roles = authRoleDao.listAuthRole(roleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        roleIds = roles.stream().map(AuthRoleEntity::getId).collect(Collectors.toSet());
        List<AuthRolePermEntity> rolePerms = authRolePermDao.listAuthRolePermission(roleIds);
        if (CollectionUtils.isEmpty(rolePerms)) {
            return Collections.emptyList();
        }
        Set<Long> permIds = rolePerms.stream().map(AuthRolePermEntity::getPermId).collect(Collectors.toSet());
        if (permIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AuthPermEntity> perms = authPermDao.listAuthPerm(permIds);
        if (permType == null) {
            return perms;
        }
        return perms.stream().filter(e -> permType.getCode().equals(e.getType()))
                .collect(Collectors.toList());
    }

    public List<AuthPermEntity> listUserMenu(Long userId) {
        return listUserPerm(userId, PermType.MENU);
    }

    public List<AuthPermEntity> listUserButton(Long userId, String menuCode) {
        if (StringUtils.isBlank(menuCode)) {
            return Collections.emptyList();
        }
        AuthPermEntity menu = authPermDao.getByCode(menuCode);
        if (menu == null) {
            return Collections.emptyList();
        }
        return listUserPerm(userId, PermType.BUTTON).stream()
                .filter(e -> menu.getId().equals(e.getParentId()))
                .collect(Collectors.toList());
    }

    public List<AuthPermEntity> listAuthPerm() {
        return authPermDao.listAuthPerm();
    }

    public Set<Long> listRolePermId(Long roleId) {
        return authRolePermDao.listAuthRolePerm(roleId).stream()
                .map(AuthRolePermEntity::getPermId)
                .collect(Collectors.toSet());
    }

    public AuthUserEntity getAuthUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return authUserDao.getById(userId);
    }

    public AuthRoleEntity getAuthRole(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return authRoleDao.getById(roleId);
    }

    public AuthPermEntity getAuthPerm(Long permId) {
        if (permId == null) {
            return null;
        }
        return authPermDao.getById(permId);
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
