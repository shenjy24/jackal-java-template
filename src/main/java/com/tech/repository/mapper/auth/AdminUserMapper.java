package com.tech.repository.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tech.repository.entity.auth.AdminUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUserEntity> {
}
