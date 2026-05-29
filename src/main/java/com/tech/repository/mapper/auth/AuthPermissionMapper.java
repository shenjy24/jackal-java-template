package com.tech.repository.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tech.repository.entity.auth.AuthPermissionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthPermissionMapper extends BaseMapper<AuthPermissionEntity> {
}
