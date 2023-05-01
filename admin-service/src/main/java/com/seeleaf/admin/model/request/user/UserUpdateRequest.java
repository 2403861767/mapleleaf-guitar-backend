package com.seeleaf.admin.model.request.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 年龄
     */
    private Integer age;
    /**
     * 性别
     */
    private String gender;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户状态 0表示正常,1表示会员，2表示封号
     */
    private Integer userStatus;

    /**
     * 用户角色：user/genealogist/sharer/admin/superAdmin
     */
    private String userRole;
}
