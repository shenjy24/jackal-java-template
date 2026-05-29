package com.tech.repository.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tech.repository.entity.user.UserAccountEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户账号表 Mapper 接口
 * </p>
 *
 * @author shenjy
 * @since 2025-08-01
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccountEntity> {

}
