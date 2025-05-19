package com.work.userservice.mapper;

import com.work.userservice.dto.UserGetDTO;
import com.work.userservice.dto.UserLoginDTO;
import com.work.userservice.entity.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public Users getUserById(String id);
    public Users login(String id, String password);
    public Boolean register(Users users);
    public String getUserPassword(String id);
    public Integer countPerson(Integer role);
}
