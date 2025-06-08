package com.work.userservice.service;

import com.work.userservice.dto.AdminLoginDTO;

public interface AdminService {
    //用户登录
    public AdminLoginDTO login(String id, String password);
}
