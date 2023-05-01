package com.seeleaf.admin.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddRequest implements Serializable {
    private static final long serialVersionUID = 5990368810850271645L;
    private String userAccount;
    private String gender;
    private Integer age;
    private String userRole;
}
