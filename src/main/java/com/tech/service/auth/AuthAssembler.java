package com.tech.service.auth;

import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.model.vo.auth.AuthMenuVo;
import com.tech.repository.model.vo.auth.AuthPermVo;
import com.tech.repository.model.vo.auth.AuthRoleVo;
import com.tech.repository.model.vo.auth.AuthUserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAssembler {

    public AuthUserVo toAuthUserVo(AuthUserEntity user) {
        if (user == null) {
            return null;
        }
        AuthUserVo authUserVo = new AuthUserVo()
                .setId(user.getId())
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar())
                .setAccount(user.getAccount());
        return authUserVo;
    }

    public List<AuthUserVo> toAuthUserVoList(List<AuthUserEntity> users) {
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

    public List<AuthRoleVo> toAuthRoleVoList(List<AuthRoleEntity> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(this::toAuthRoleVo).collect(Collectors.toList());
    }

    public AuthPermVo toAuthPermVo(AuthPermEntity perm) {
        if (perm == null) {
            return null;
        }
        AuthPermVo vo = new AuthPermVo();
        vo.setId(perm.getId());
        vo.setParentId(perm.getParentId());
        vo.setCode(perm.getCode());
        vo.setName(perm.getName());
        vo.setType(perm.getType());
        vo.setRemark(perm.getRemark());
        return vo;
    }

    public List<AuthPermVo> toAuthPermVo(List<AuthPermEntity> perms) {
        if (CollectionUtils.isEmpty(perms)) {
            return Collections.emptyList();
        }
        return perms.stream().map(this::toAuthPermVo).collect(Collectors.toList());
    }

    public List<AuthMenuVo> toAuthMenuTree(List<AuthPermEntity> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        Map<Long, AuthMenuVo> menuMap = menus.stream()
                .sorted(Comparator.comparing(AuthPermEntity::getCode))
                .collect(Collectors.toMap(AuthPermEntity::getId, this::toAuthMenuVo, (a, b) -> a, LinkedHashMap::new));
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

    private AuthMenuVo toAuthMenuVo(AuthPermEntity menu) {
        AuthMenuVo vo = new AuthMenuVo();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setCode(menu.getCode());
        vo.setName(menu.getName());
        vo.setRemark(menu.getRemark());
        vo.setChildren(new ArrayList<>());
        return vo;
    }
}
