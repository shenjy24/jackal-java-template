package com.tech.repository.dao.auth;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.mapper.auth.AuthPermMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class AuthPermDao extends ServiceImpl<AuthPermMapper, AuthPermEntity> {
    public List<AuthPermEntity> listAuthPermission(Set<Long> permIds) {
        if (CollectionUtils.isEmpty(permIds)) {
            return Collections.emptyList();
        }
        return listByIds(permIds);
    }
}
