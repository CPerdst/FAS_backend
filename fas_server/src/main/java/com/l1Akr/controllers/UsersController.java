package com.l1Akr.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.l1Akr.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.l1Akr.common.result.Result;
import com.l1Akr.vo.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UsersController {

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/user/login")
    public Result<String> login(@RequestBody User user) {
        log.info("Login User: {}", user);
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        String token = jwtUtils.generateToken(user.getUsername(), map);
        return Result.success(token);
    }


}
