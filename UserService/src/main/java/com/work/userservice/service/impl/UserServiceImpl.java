package com.work.userservice.service.impl;

import com.work.commonconfig.config.filter.JwtFilter;
import com.work.commonconfig.exception.MyException;
import com.work.userservice.dto.UserGetDTO;
import com.work.userservice.dto.UserLoginDTO;
import com.work.userservice.entity.Users;
import com.work.userservice.mapper.UserMapper;
import com.work.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    //用户信息展示
    @Override
    public UserGetDTO getUserById(String token) {
        if(!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效",401);
        UserGetDTO userGetDTO = new UserGetDTO();
        String id = JwtFilter.getUserIdFromToken(token);
        Users users = userMapper.getUserById(id);
        if(users == null)
            MyException.throwError("用户不存在",404);
        userGetDTO.setId(users.getId());
        userGetDTO.setName(users.getName());
        userGetDTO.setRole(users.getRole());
        userGetDTO.setGender(users.getGender());
        return userGetDTO;
    }

    //用户登录
    @Override
    public UserLoginDTO login(String id, String password) {
        String lockPass = userMapper.getUserPassword(id);
        if(lockPass == null){
            MyException.throwError("用户不存在",404);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(password, lockPass)){
            UserLoginDTO userLoginDTO = new UserLoginDTO();
            Users users1 = userMapper.login(id, lockPass);
            String token = JwtFilter.generateToken(users1.getId(),users1.getRole());
            userLoginDTO.setId(users1.getId());
            userLoginDTO.setName(users1.getName());
            userLoginDTO.setRole(users1.getRole());
            userLoginDTO.setToken(token);
            return userLoginDTO;
        }else{
            MyException.throwError("密码错误",401);
        }
        return null;
    }

    //注册
    @Override
    public String register(Users users) {
        // 校验 role 合法性
        if (users.getRole() == null || (users.getRole() != 1 && users.getRole() != 2))
            MyException.throwError("非法角色",400);


        // 校验密码长度
        if (users.getPassword() == null || users.getPassword().length() < 6 || users.getPassword().length() > 13)
            MyException.throwError("密码必须在6位和13位之间",400);

        // 统计当前该角色已有的用户数
        Integer count = userMapper.countPerson(users.getRole());
        if (count == null)
            count = 0;
        count += 1;

        // 生成账号 ID（总长11位：角色 + 补零 + count）
        String id = users.getRole().toString();
        for (int i = 0; i < 10 - count.toString().length(); i++) {
            id += "0";
        }
        id += count.toString();
        users.setId(id);

        // 若 name 为空，根据角色赋默认值
        if (users.getName() == null || users.getName().trim().isEmpty()) {
            String prefix = users.getRole() == 1 ? "操作员" : "质检员";
            users.setName(prefix + String.format("%03d", count));
        }

        // 设置默认 gender
        if (users.getGender() == null)
            users.setGender(true);

        // 加密密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        // 调试输出
        System.out.println("注册用户：" + users);

        // 注册用户
        int result = userMapper.register(users);
        if (result > 0) {
            return id;
        } else {
            MyException.throwError("注册失败", 500);
            return null; // 理论上不会走到这里
        }
    }

    //统计相同权限的总人数
    @Override
    public Integer countPerson(Integer role){
        // 校验 role 合法性
        if (role == null || role != 1 && role != 2)
            MyException.throwError("非法角色",400);
        return userMapper.countPerson(role);
    }

    //用户修改密码
    @Override
    public Boolean updatePassword(String token, String oldPassword, String newPassword) {
        if(!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效",401);
        if (oldPassword == null || oldPassword.isBlank())
            MyException.throwError("旧密码不能为空", 400);
        if (newPassword == null || newPassword.isBlank())
            MyException.throwError("新密码不能为空", 400);
        String id = JwtFilter.getUserIdFromToken(token);
        String lockPass = userMapper.getUserPassword(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(oldPassword, lockPass)) {
            // 校验密码长度
            if (newPassword.length() < 6 || newPassword.length() > 13)
                MyException.throwError("新密码必须在6位和13位之间",400);
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            Integer rows = userMapper.updatePassword(id, encodedNewPassword);
            return rows != null && rows > 0;
        }
        else {
            MyException.throwError("原密码错误", 401);
            return false;
        }
    }

}

