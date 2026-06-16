package com.tech.service.auth;

import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.model.vo.auth.AuthMenuVo;
import com.tech.repository.model.vo.auth.AuthPermVo;
import com.tech.repository.model.vo.auth.AuthRoleVo;
import com.tech.repository.model.vo.auth.AuthUserVo;
import com.tech.repository.model.vo.auth.AuthUserRoleVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAssembler {

    public AuthUserVo toAuthUserVo(AuthUserEntity user) {
        return toAuthUserVo(user, Collections.emptyList());
    }

    public AuthUserVo toAuthUserVo(AuthUserEntity user, List<AuthRoleEntity> roles) {
        if (user == null) {
            return null;
        }
        AuthUserVo vo = new AuthUserVo()
                .setId(user.getId())
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar())
                .setAccount(user.getAccount())
                .setRoles(toAuthUserRoleVo(roles));
        return vo;
    }

    public List<AuthUserVo> toAuthUserVo(List<AuthUserEntity> users) {
        return toAuthUserVo(users, Collections.emptyMap());
    }

    public List<AuthUserVo> toAuthUserVo(List<AuthUserEntity> users, Map<Long, List<AuthRoleEntity>> roleMap) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        Map<Long, List<AuthRoleEntity>> userRoleMap = roleMap == null ? Collections.emptyMap() : roleMap;
        return users.stream()
                .map(e -> toAuthUserVo(e, userRoleMap.get(e.getId())))
                .collect(Collectors.toList());
    }

    public AuthUserRoleVo toAuthUserRoleVo(AuthRoleEntity role) {
        if (role == null) {
            return null;
        }
        AuthUserRoleVo vo = new AuthUserRoleVo()
                .setId(role.getId())
                .setName(role.getName());
        return vo;
    }

    public List<AuthUserRoleVo> toAuthUserRoleVo(List<AuthRoleEntity> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(this::toAuthUserRoleVo).collect(Collectors.toList());
    }

    public AuthRoleVo toAuthRoleVo(AuthRoleEntity role) {
        if (role == null) {
            return null;
        }
        AuthRoleVo vo = new AuthRoleVo()
                .setId(role.getId())
                .setName(role.getName())
                .setRemark(role.getRemark());
        return vo;
    }

    public List<AuthRoleVo> toAuthRoleVo(List<AuthRoleEntity> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(this::toAuthRoleVo).collect(Collectors.toList());
    }

    public AuthPermVo toAuthPermVo(AuthPermEntity perm) {
        if (perm == null) {
            return null;
        }
        AuthPermVo vo = new AuthPermVo()
                .setId(perm.getId())
                .setParentId(perm.getParentId())
                .setCode(perm.getCode())
                .setName(perm.getName())
                .setType(perm.getType())
                .setIcon(perm.getIcon())
                .setPath(perm.getPath())
                .setComponent(perm.getComponent())
                .setSort(perm.getSort())
                .setRemark(perm.getRemark());
        return vo;
    }

    public List<AuthPermVo> toAuthPermVo(List<AuthPermEntity> perms) {
        if (CollectionUtils.isEmpty(perms)) {
            return Collections.emptyList();
        }
        return perms.stream().map(this::toAuthPermVo).collect(Collectors.toList());
    }

    public List<AuthMenuVo> toAuthMenuTree(List<AuthPermEntity> menus) {
        return buildAuthPermTree(menus, Collections.emptySet());
    }

    public List<AuthMenuVo> toAuthPermTree(List<AuthPermEntity> perms) {
        return buildAuthPermTree(perms, Collections.emptySet());
    }

    public List<AuthMenuVo> toAuthPermTree(List<AuthPermEntity> perms, Set<Long> checkedIds) {
        return buildAuthPermTree(perms, checkedIds == null ? Collections.emptySet() : checkedIds);
    }

    private List<AuthMenuVo> buildAuthPermTree(List<AuthPermEntity> menus, Set<Long> checkedIds) {
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        Map<Long, AuthMenuVo> menuMap = menus.stream()
                .sorted(Comparator.comparing(AuthPermEntity::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(AuthPermEntity::getId))
                .collect(Collectors.toMap(AuthPermEntity::getId, e -> toAuthMenuVo(e, checkedIds), (a, b) -> a, LinkedHashMap::new));
        List<AuthMenuVo> roots = new ArrayList<>();
        for (AuthMenuVo menu : menuMap.values()) {
            AuthMenuVo parent = menuMap.get(menu.getParentId());
            if (parent == null) {
                roots.add(menu);
            } else {
                parent.getChildren().add(menu);
            }
        }
        return roots;
    }

    private AuthMenuVo toAuthMenuVo(AuthPermEntity menu, Set<Long> checkedIds) {
        AuthMenuVo vo = new AuthMenuVo()
                .setId(menu.getId())
                .setParentId(menu.getParentId())
                .setCode(menu.getCode())
                .setName(menu.getName())
                .setIcon(menu.getIcon())
                .setPath(menu.getPath())
                .setComponent(menu.getComponent())
                .setSort(menu.getSort())
                .setRemark(menu.getRemark())
                .setChecked(BooleanUtils.toInteger(checkedIds.contains(menu.getId())))
                .setChildren(new ArrayList<>());
        return vo;
    }
}
