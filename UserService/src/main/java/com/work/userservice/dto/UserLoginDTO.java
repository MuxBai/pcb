package com.work.userservice.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String id;
    private String name;
    private Integer role;
}
