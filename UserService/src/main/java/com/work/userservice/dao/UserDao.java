package com.work.userservice.dao;


import com.work.userservice.entity.Users;

public interface UserDao {
    public Users getUserById(String id);
    public Users login(String id, String password);
//    public Boolean register(Users users);
    public String getUserPassword(String id);
    public Integer countPerson(Integer role);
}
