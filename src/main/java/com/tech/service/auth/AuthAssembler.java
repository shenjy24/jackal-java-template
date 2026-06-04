package com.tech.service.auth;

import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.model.vo.auth.AuthUserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAssembler {

    public AuthUserVo toAuthUserVo(AuthUserEntity user) {
        if (user == null) {
            return null;
        }
        AuthUserVo authUserVo = new AuthUserVo()
                .setId(user.getId())
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar());
        return authUserVo;
    }
}
