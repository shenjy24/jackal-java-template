package com.tech.repository.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tech.repository.entity.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
