package com.work.userservice.dto;

import lombok.Data;

@Data
public class UserGetDTO {
    private String id;
    private Boolean gender;
    private String name;
    private Integer role;
}
