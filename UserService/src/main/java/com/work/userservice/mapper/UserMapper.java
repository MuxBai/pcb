package com.work.userservice.mapper;

import com.work.userservice.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public Users getUserById(String id);
    public Users login(String id, String password);
    public Integer register(Users users);
    public String getUserPassword(String id);
    public Integer countPerson(Integer role);
    public Integer updatePassword(@Param("id") String id, @Param("newPassword") String newPassword);
}
