package com.tech.repository.dao.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tech.repository.entity.user.UserEntity;
import com.tech.repository.mapper.user.UserMapper;
import org.springframework.stereotype.Service;

/**
 * UserManager
 *
 * @author Jonas
 * @since 2026-05-23
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, UserEntity> {
}
