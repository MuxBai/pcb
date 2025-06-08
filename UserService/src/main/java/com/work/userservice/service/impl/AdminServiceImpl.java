package com.work.userservice.service.impl;

import com.work.commonconfig.config.filter.JwtFilter;
import com.work.commonconfig.exception.MyException;
import com.work.userservice.dto.AdminLoginDTO;
import com.work.userservice.entity.Admin;
import com.work.userservice.mapper.AdminMapper;
import com.work.userservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    //登录
    public AdminLoginDTO login(String id, String password){
        String lockPass = adminMapper.getAdminPassword(id);
        if(lockPass == null){
            MyException.throwError("用户不存在",404);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(password, lockPass)){
            AdminLoginDTO adminLoginDTO = new AdminLoginDTO();
            Admin admin1 = adminMapper.login(id, lockPass);
            String token = JwtFilter.generateToken(admin1.getId(),null);
            adminLoginDTO.setId(admin1.getId());
            adminLoginDTO.setName(admin1.getName());
            return adminLoginDTO;
        }else{
            MyException.throwError("密码错误",401);
        }
        return null;
    }

}
