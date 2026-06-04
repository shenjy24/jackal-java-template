package com.tech.service.auth;

import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.model.vo.auth.AuthMenuVo;
import com.tech.repository.model.vo.auth.AuthPermVo;
import com.tech.repository.model.vo.auth.AuthRoleVo;
import com.tech.repository.model.vo.auth.AuthUserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAssembler {

    public AuthUserVo toAuthUserVo(AuthUserEntity user) {
        if (user == null) {
            return null;
        }
        AuthUserVo vo = new AuthUserVo()
                .setId(user.getId())
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar())
                .setAccount(user.getAccount());
        return vo;
    }

    public List<AuthUserVo> toAuthUserVo(List<AuthUserEntity> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        return users.stream().map(this::toAuthUserVo).collect(Collectors.toList());
    }

    public AuthRoleVo toAuthRoleVo(AuthRoleEntity role) {
        if (role == null) {
            return null;
        }
        AuthRoleVo vo = new AuthRoleVo();
        vo.setId(role.getId());
        vo.setCode(role.getCode());
        vo.setName(role.getName());
        vo.setRemark(role.getRemark());
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
                .setSort(menu.getSort())
                .setRemark(menu.getRemark())
                .setChecked(BooleanUtils.toInteger(checkedIds.contains(menu.getId())))
                .setChildren(new ArrayList<>());
        return vo;
    }
}
