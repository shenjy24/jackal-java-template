package com.tech.service.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tech.common.enums.auth.PermType;
import com.tech.repository.dao.auth.*;
import com.tech.repository.entity.auth.*;
import com.tech.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        return TimeUtil.currentDateTime().isAfter(token.getExpireTime());
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
        List<AuthRoleEntity> roles = authRoleDao.listByIds(roleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        roleIds = roles.stream().map(AuthRoleEntity::getId).collect(Collectors.toSet());
        List<AuthRolePermEntity> rolePerms = authRolePermDao.listAuthRolePerm(roleIds);
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
        Set<Integer> menuTypes = Set.of(PermType.DIRECTORY.getCode(), PermType.MENU.getCode());
        return listUserPerm(userId, null).stream()
                .filter(e -> menuTypes.contains(e.getType()))
                .collect(Collectors.toList());
    }

    public List<AuthPermEntity> listUserButton(Long userId, Long permId) {
        if (userId == null || permId == null) {
            return Collections.emptyList();
        }
        AuthPermEntity menu = authPermDao.getById(permId);
        if (menu == null) {
            return Collections.emptyList();
        }
        return listUserPerm(userId, PermType.BUTTON).stream()
                .filter(e -> menu.getId().equals(e.getParentId()))
                .collect(Collectors.toList());
    }

    public List<AuthPermEntity> listAuthPerm() {
        return authPermDao.list();
    }

    public Set<Long> listRolePermId(Long roleId) {
        return authRolePermDao.listAuthRolePerm(roleId).stream()
                .map(AuthRolePermEntity::getPermId)
                .collect(Collectors.toSet());
    }

    public List<AuthRoleEntity> listUserRole(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        Set<Long> roleIds = authUserRoleDao.listAuthUserRole(userId).stream()
                .map(AuthUserRoleEntity::getRoleId)
                .collect(Collectors.toSet());
        return authRoleDao.listByIds(roleIds);
    }

    public Map<Long, List<AuthRoleEntity>> listUserRoleMap(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<AuthUserRoleEntity> userRoles = authUserRoleDao.listAuthUserRole(userIds);
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyMap();
        }
        Set<Long> roleIds = userRoles.stream()
                .map(AuthUserRoleEntity::getRoleId)
                .collect(Collectors.toSet());
        Map<Long, AuthRoleEntity> roleMap = authRoleDao.listByIds(roleIds).stream()
                .collect(Collectors.toMap(AuthRoleEntity::getId, e -> e));
        return userRoles.stream()
                .filter(e -> roleMap.containsKey(e.getRoleId()))
                .collect(Collectors.groupingBy(AuthUserRoleEntity::getUserId,
                        Collectors.mapping(e -> roleMap.get(e.getRoleId()), Collectors.toList())));
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

    public IPage<AuthUserEntity> queryAuthUser(String nickname, Integer pageNum, Integer pageSize) {
        return authUserDao.queryAuthUser(nickname, pageNum, pageSize);
    }

    public IPage<AuthRoleEntity> queryAuthRole(String name, Integer pageNum, Integer pageSize) {
        return authRoleDao.queryAuthRole(name, pageNum, pageSize);
    }

    public IPage<AuthPermEntity> queryAuthPerm(String code, String name, Integer type, Integer pageNum, Integer pageSize) {
        return authPermDao.queryAuthPerm(code, name, type, pageNum, pageSize);
    }
}
