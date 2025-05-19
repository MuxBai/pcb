package com.work.userservice.service;

import com.work.userservice.dto.UserGetDTO;
import com.work.userservice.dto.UserLoginDTO;
import com.work.userservice.entity.Users;

public interface UserService {

    //通过id获取用户信息
    public UserGetDTO getUserById(String id);

    //登录
    public UserLoginDTO login(String id, String password);

    //注册
    public Boolean register(Users users);
}
