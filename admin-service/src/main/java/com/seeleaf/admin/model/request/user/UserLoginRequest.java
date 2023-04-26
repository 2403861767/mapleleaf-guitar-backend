package com.seeleaf.admin.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -7658022289888539332L;
    private String userAccount;
    private String userPassword;
}
