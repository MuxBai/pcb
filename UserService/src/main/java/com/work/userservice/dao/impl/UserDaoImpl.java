package com.work.userservice.dao.impl;

import com.work.userservice.dao.UserDao;
import com.work.userservice.entity.Users;
import com.work.userservice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private UserMapper userMapper;

    //获取用户信息
    @Override
    public Users getUserById(String id){
        return userMapper.getUserById(id);
    }

    //用户登录
    @Override
    public Users login(String id, String password){
        return userMapper.login(id, password);
    }

   /* //用户注册
    @Override
    public Integer register(Users users){
        return userMapper.register(users);
    }*/

    @Override
    public String getUserPassword(String id){
        return userMapper.getUserPassword(id);
    }

    @Override
    public Integer countPerson(Integer role){
        return userMapper.countPerson(role);
    }

}
