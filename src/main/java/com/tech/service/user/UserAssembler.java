package com.tech.service.user;

import com.tech.repository.model.vo.user.UserVo;
import com.tech.repository.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAssembler {

    public UserVo toUserVo(UserEntity user) {
        if (user == null) {
            return null;
        }
        return new UserVo()
                .setId(user.getId())
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar())
                .setAddress(user.getAddress())
                .setGender(user.getGender())
                .setPhone(user.getPhone())
                .setBirthday(user.getBirthday());
    }
}
