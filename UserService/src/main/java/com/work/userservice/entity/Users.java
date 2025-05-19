package com.work.userservice.entity;

import lombok.Data;

@Data
public class Users {
    private String id;
    private String name;
    private Boolean gender;
    private Integer  role;
    private String password;
}
