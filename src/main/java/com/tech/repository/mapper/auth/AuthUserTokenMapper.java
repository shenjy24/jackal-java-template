package com.tech.repository.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tech.repository.entity.auth.AuthUserTokenEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthUserTokenMapper extends BaseMapper<AuthUserTokenEntity> {
}
