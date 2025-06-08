package com.work.userservice.mapper;

import com.work.userservice.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    public Admin login(String id, String password);
    public String getAdminPassword(String id);
    public Admin getAdminById(String id);
}
