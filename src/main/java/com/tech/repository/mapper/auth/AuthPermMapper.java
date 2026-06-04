package com.tech.repository.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tech.repository.entity.auth.AuthPermEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthPermMapper extends BaseMapper<AuthPermEntity> {
}
