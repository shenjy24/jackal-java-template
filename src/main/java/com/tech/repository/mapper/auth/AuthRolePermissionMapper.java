package com.tech.repository.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tech.repository.entity.auth.AuthRolePermissionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRolePermissionMapper extends BaseMapper<AuthRolePermissionEntity> {
}
