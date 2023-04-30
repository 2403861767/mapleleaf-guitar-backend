package com.seeleaf.admin.model.request.user;

import lombok.Data;

@Data
public class UserAddRequest {
    private String userAccount;
    private String gender;
    private Integer age;
    private String userRole;
}
