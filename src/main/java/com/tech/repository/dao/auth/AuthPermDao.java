package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.mapper.auth.AuthPermMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class AuthPermDao extends ServiceImpl<AuthPermMapper, AuthPermEntity> {
    public AuthPermEntity getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AuthPermEntity>().eq(AuthPermEntity::getCode, code));
    }

    public List<AuthPermEntity> listAuthPerm(Set<Long> permIds) {
        if (CollectionUtils.isEmpty(permIds)) {
            return Collections.emptyList();
        }
        return listByIds(permIds);
    }

    public IPage<AuthPermEntity> queryAuthPerm(String code, String name, Integer type, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AuthPermEntity> wrapper = new LambdaQueryWrapper<AuthPermEntity>()
                .like(StringUtils.isNotBlank(code), AuthPermEntity::getCode, code)
                .like(StringUtils.isNotBlank(name), AuthPermEntity::getName, name)
                .eq(type != null, AuthPermEntity::getType, type)
                .orderByDesc(AuthPermEntity::getId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
