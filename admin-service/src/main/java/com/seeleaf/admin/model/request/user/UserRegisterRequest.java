package com.seeleaf.admin.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = 6461918582717624240L;
    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
