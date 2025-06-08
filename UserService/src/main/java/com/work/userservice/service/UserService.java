package com.work.userservice.service;

import com.work.userservice.dto.UserGetDTO;
import com.work.userservice.dto.UserLoginDTO;
import com.work.userservice.entity.Users;

public interface UserService {

    //通过id获取用户信息
    public UserGetDTO getUserById(String token);

    //登录
    public UserLoginDTO login(String id, String password);

    //注册
    public String register(Users users);

    //统计相同权限的总人数
    public Integer countPerson(Integer role);

    //修改用户密码
    public Boolean updatePassword(String token, String oldPassword, String newPassword);
}
