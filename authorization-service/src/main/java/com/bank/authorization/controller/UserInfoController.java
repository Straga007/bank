package com.bank.authorization.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/userinfo")
    public Map<String, Object> userInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return Collections.emptyMap();
        }
        return principal.getAttributes();
    }
}