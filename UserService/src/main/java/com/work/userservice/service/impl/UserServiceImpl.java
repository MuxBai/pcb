package com.work.userservice.service.impl;

import com.work.userservice.dao.UserDao;
import com.work.userservice.dto.UserGetDTO;
import com.work.userservice.dto.UserLoginDTO;
import com.work.userservice.entity.Users;
import com.work.userservice.exception.LoginFailedException;
import com.work.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public UserGetDTO getUserById(String id) {
        UserGetDTO userGetDTO = new UserGetDTO();
        Users users = userDao.getUserById(id);
        userGetDTO.setId(users.getId());
        userGetDTO.setName(users.getName());
        userGetDTO.setRole(users.getRole());
        userGetDTO.setGender(users.getGender());
        return userGetDTO;
    }

    @Override
    public UserLoginDTO login(String id, String password) {
        String lockPass = userDao.getUserPassword(id);
        if(lockPass == null){
            throw new LoginFailedException("用户不存在");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(password, lockPass)){
            UserLoginDTO userLoginDTO = new UserLoginDTO();
            Users users1 = userDao.login(id, lockPass);
            userLoginDTO.setId(users1.getId());
            userLoginDTO.setName(users1.getName());
            userLoginDTO.setRole(users1.getRole());
            return userLoginDTO;
        }else{
            throw new LoginFailedException("密码错误");
        }
    }

    @Override
    public Boolean register(Users users) {

        //处理生成账号
        Integer count = userDao.countPerson(users.getRole());
//        System.out.println("616161616       "+count);
        if (count == null)
            count = 0;
        count += 1;
        String id;
        if(users.getRole() == null)
            id = "1";
        else
            id = users.getRole().toString();//账号总长11位长度
        for(int i = 0;i < 10-count.toString().length();i++){
            id += "0";
        }
        id+=count.toString();
        users.setId(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        users.setPassword(passwordEncoder.encode(users.getPassword()));
       System.out.println("111111      "+users);
        return userDao.register(users);
    }
}

